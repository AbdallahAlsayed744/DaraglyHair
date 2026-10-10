package com.daragly.daraglyhair.common.domain.usecase

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

abstract class BaseUseCase<out Domain, in Body> {
    abstract suspend operator fun invoke(body: Body): Domain

    /**
     * Executes [codeBlock] as a cold flow that emits:
     * Loading(true) → Success(result) → Loading(false)
     * or
     * Loading(true) → Failure(exception) → Loading(false)
     */
    fun <Domain> flowExecute(
        codeBlock: suspend () -> Domain,
    ): Flow<Resource<Domain>> = channelFlow {
        send(Resource.Success(codeBlock.invoke()))
        send(Resource.Loading(isLoading = false))
    }.onStart {
        emit(Resource.Loading(isLoading = true))
    }.catch { exception ->
        emit(handleFailure(exception))
        emit(Resource.Loading(isLoading = false))
    }.flowOn(Dispatchers.Default)

    /**
     * Wraps an upstream [dataFlow] with Loading / Success / Failure states.
     */
    fun <Domain> streamExecute(
        dataFlow: Flow<Domain>,
    ): Flow<Resource<Domain>> = dataFlow
        .map { data -> Resource.Success(data) as Resource<Domain> }
        .onStart { emit(Resource.Loading(true)) }
        .catch { exception ->
            emit(handleFailure(exception))
            emit(Resource.Loading(false))
        }
        .flowOn(Dispatchers.Default)

    /**
     * Executes [codeBlock] outside of a flow, returning a single [Resource].
     */
    suspend fun <Domain> nonFlowExecute(
        codeBlock: suspend () -> Domain,
    ): Resource<Domain> = runCatching {
        Resource.Success(codeBlock.invoke())
    }.getOrElse { handleFailure(it) }

    private fun handleFailure(exception: Throwable): Resource.Failure {
        val appException = exception as? DaraglyHairException
            ?: DaraglyHairException.UnKnown(message = exception.message)
        return Resource.Failure(appException)
    }
}

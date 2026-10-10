package com.daragly.daraglyhair.feature.authentication.domain.interactors

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.authentication.data.request.RegisterRequest
import com.daragly.daraglyhair.feature.authentication.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class RegisterUseCase(
    private val repository: IAuthRepository,
) : BaseUseCase<Flow<Resource<String>>, RegisterRequest>() {
    override suspend fun invoke(body: RegisterRequest): Flow<Resource<String>> = flowExecute {
        val errors = body.validateFields()
        if (errors.isNotEmpty()) throw DaraglyHairException.Local.RequestValidation(errors = errors)
        repository.register(body)
    }
}

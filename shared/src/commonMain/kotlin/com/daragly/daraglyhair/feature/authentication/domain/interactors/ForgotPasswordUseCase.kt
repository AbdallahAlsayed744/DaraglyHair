package com.daragly.daraglyhair.feature.authentication.domain.interactors

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.authentication.data.request.ForgotPasswordRequest
import com.daragly.daraglyhair.feature.authentication.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class ForgotPasswordUseCase(
    private val repository: IAuthRepository,
) : BaseUseCase<Flow<Resource<String>>, ForgotPasswordRequest>() {
    override suspend fun invoke(body: ForgotPasswordRequest): Flow<Resource<String>> = flowExecute {
        if (!body.email.contains("@") || body.email.length < 5)
            throw DaraglyHairException.Local.RequestValidation(message = "Please enter a valid email address")
        repository.forgotPassword(body)
    }
}

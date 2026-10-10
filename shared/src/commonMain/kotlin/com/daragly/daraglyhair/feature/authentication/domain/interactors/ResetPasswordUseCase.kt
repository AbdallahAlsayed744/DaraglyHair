package com.daragly.daraglyhair.feature.authentication.domain.interactors

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.authentication.data.request.ResetPasswordRequest
import com.daragly.daraglyhair.feature.authentication.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class ResetPasswordUseCase(
    private val repository: IAuthRepository,
) : BaseUseCase<Flow<Resource<String>>, ResetPasswordRequest>() {
    override suspend fun invoke(body: ResetPasswordRequest): Flow<Resource<String>> = flowExecute {
        if (body.password.length < 8)
            throw DaraglyHairException.Local.RequestValidation(message = "Password must be at least 8 characters")
        if (body.token.isBlank())
            throw DaraglyHairException.Local.RequestValidation(message = "Reset token is required")
        repository.resetPassword(body)
    }
}

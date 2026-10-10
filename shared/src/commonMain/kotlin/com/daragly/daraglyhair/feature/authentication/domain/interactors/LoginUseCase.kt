package com.daragly.daraglyhair.feature.authentication.domain.interactors

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.authentication.data.request.LoginRequest
import com.daragly.daraglyhair.feature.authentication.domain.model.AuthToken
import com.daragly.daraglyhair.feature.authentication.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow

class LoginUseCase(
    private val repository: IAuthRepository,
) : BaseUseCase<Flow<Resource<AuthToken>>, LoginRequest>() {
    override suspend fun invoke(body: LoginRequest): Flow<Resource<AuthToken>> = flowExecute {
        val errors = body.validateFields()
        if (errors.isNotEmpty()) throw DaraglyHairException.Local.RequestValidation(errors = errors)
        repository.login(body)
    }
}

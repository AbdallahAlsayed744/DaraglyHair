package com.daragly.daraglyhair.feature.authentication.data.repository

import com.daragly.daraglyhair.feature.authentication.data.mapper.AuthMapper
import com.daragly.daraglyhair.feature.authentication.data.remote.AuthRemoteDataSource
import com.daragly.daraglyhair.feature.authentication.data.request.ForgotPasswordRequest
import com.daragly.daraglyhair.feature.authentication.data.request.LoginRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RefreshTokenRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RegisterRequest
import com.daragly.daraglyhair.feature.authentication.data.request.ResetPasswordRequest
import com.daragly.daraglyhair.feature.authentication.domain.model.AuthToken
import com.daragly.daraglyhair.feature.authentication.domain.repository.IAuthRepository
import com.daragly.daraglyhair.feature.user.domain.usecase.SaveIsAuthenticatedUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.SaveTokenUseCase

class AuthRepository(
    private val remote: AuthRemoteDataSource,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val saveIsAuthenticatedUseCase: SaveIsAuthenticatedUseCase,
) : IAuthRepository {

    override suspend fun login(request: LoginRequest): AuthToken {
        val dto   = remote.login(request)
        val token = AuthMapper.toDomain(dto)
        // Persist token and auth flag using use cases
        saveTokenUseCase.invoke(token.token)
        saveIsAuthenticatedUseCase.invoke(true)
        return token
    }

    override suspend fun register(request: RegisterRequest): String =
        remote.register(request)

    override suspend fun forgotPassword(request: ForgotPasswordRequest): String =
        remote.forgotPassword(request)

    override suspend fun resetPassword(request: ResetPasswordRequest): String =
        remote.resetPassword(request)

    override suspend fun refreshToken(request: RefreshTokenRequest): AuthToken {
        val dto   = remote.refreshToken(request)
        val token = AuthMapper.toDomain(dto)
        // Persist token and auth flag using use cases
        saveTokenUseCase.invoke(token.token)
        saveIsAuthenticatedUseCase.invoke(true)
        return token
    }
}

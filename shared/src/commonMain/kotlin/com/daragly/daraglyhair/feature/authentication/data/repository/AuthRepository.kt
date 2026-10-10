package com.daragly.daraglyhair.feature.authentication.data.repository

import com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceEnum
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import com.daragly.daraglyhair.feature.authentication.data.mapper.AuthMapper
import com.daragly.daraglyhair.feature.authentication.data.remote.AuthRemoteDataSource
import com.daragly.daraglyhair.feature.authentication.data.request.ForgotPasswordRequest
import com.daragly.daraglyhair.feature.authentication.data.request.LoginRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RefreshTokenRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RegisterRequest
import com.daragly.daraglyhair.feature.authentication.data.request.ResetPasswordRequest
import com.daragly.daraglyhair.feature.authentication.domain.model.AuthToken
import com.daragly.daraglyhair.feature.authentication.domain.repository.IAuthRepository

class AuthRepository(
    private val remote: AuthRemoteDataSource,
    private val local: ILocalDataSourceProvider,
) : IAuthRepository {

    override suspend fun login(request: LoginRequest): AuthToken {
        val dto   = remote.login(request)
        val token = AuthMapper.toDomain(dto)
        // Persist token and auth flag locally
        local.save(LocalDataSourceEnum.ACCESS_TOKEN, token.token, String::class)
        local.save(LocalDataSourceEnum.IS_AUTHENTICATED, true, Boolean::class)
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
        local.save(LocalDataSourceEnum.ACCESS_TOKEN, token.token, String::class)
        local.save(LocalDataSourceEnum.IS_AUTHENTICATED, true, Boolean::class)
        return token
    }
}

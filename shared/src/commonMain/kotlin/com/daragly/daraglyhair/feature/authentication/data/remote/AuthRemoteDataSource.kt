package com.daragly.daraglyhair.feature.authentication.data.remote

import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.feature.authentication.data.dto.TokenDto
import com.daragly.daraglyhair.feature.authentication.data.request.ForgotPasswordRequest
import com.daragly.daraglyhair.feature.authentication.data.request.LoginRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RefreshTokenRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RegisterRequest
import com.daragly.daraglyhair.feature.authentication.data.request.ResetPasswordRequest
import kotlinx.serialization.builtins.serializer

class AuthRemoteDataSource(private val remote: IRemoteDataSourceProvider) {

    // POST api/tokens → returns token + refreshToken
    suspend fun login(request: LoginRequest): TokenDto = remote.post(
        endpoint    = LOGIN_ENDPOINT,
        headers     = mapOf(TENANT_HEADER to TENANT),
        requestBody = request,
        serializer  = TokenDto.serializer(),
    )

    // POST api/tokens/refresh
    suspend fun refreshToken(request: RefreshTokenRequest): TokenDto = remote.post(
        endpoint    = REFRESH_ENDPOINT,
        headers     = mapOf(TENANT_HEADER to TENANT),
        requestBody = request,
        serializer  = TokenDto.serializer(),
    )

    // POST api/users/self-register → returns userId string
    suspend fun register(request: RegisterRequest): String = remote.post(
        endpoint    = REGISTER_ENDPOINT,
        headers     = mapOf(TENANT_HEADER to TENANT),
        requestBody = request,
        serializer  = String.serializer(),
    )

    // POST api/users/forgot-password
    suspend fun forgotPassword(request: ForgotPasswordRequest): String = remote.post(
        endpoint    = FORGOT_PASSWORD_ENDPOINT,
        headers     = mapOf(TENANT_HEADER to TENANT),
        requestBody = request,
        serializer  = String.serializer(),
    )

    // POST api/users/reset-password (requires auth token header)
    suspend fun resetPassword(request: ResetPasswordRequest): String = remote.post(
        endpoint    = RESET_PASSWORD_ENDPOINT,
        requestBody = request,
        serializer  = String.serializer(),
    )

    companion object {
        const val TENANT_HEADER          = "tenant"
        const val TENANT                 = "root"
        const val LOGIN_ENDPOINT         = "api/tokens"
        const val REFRESH_ENDPOINT       = "api/tokens/refresh"
        const val REGISTER_ENDPOINT      = "api/users/self-register"
        const val FORGOT_PASSWORD_ENDPOINT = "api/users/forgot-password"
        const val RESET_PASSWORD_ENDPOINT  = "api/users/reset-password"
    }
}

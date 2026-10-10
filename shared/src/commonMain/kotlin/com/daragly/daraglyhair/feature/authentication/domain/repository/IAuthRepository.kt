package com.daragly.daraglyhair.feature.authentication.domain.repository

import com.daragly.daraglyhair.feature.authentication.data.request.ForgotPasswordRequest
import com.daragly.daraglyhair.feature.authentication.data.request.LoginRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RefreshTokenRequest
import com.daragly.daraglyhair.feature.authentication.data.request.RegisterRequest
import com.daragly.daraglyhair.feature.authentication.data.request.ResetPasswordRequest
import com.daragly.daraglyhair.feature.authentication.domain.model.AuthToken

interface IAuthRepository {
    suspend fun login(request: LoginRequest): AuthToken
    suspend fun register(request: RegisterRequest): String
    suspend fun forgotPassword(request: ForgotPasswordRequest): String
    suspend fun resetPassword(request: ResetPasswordRequest): String
    suspend fun refreshToken(request: RefreshTokenRequest): AuthToken
}

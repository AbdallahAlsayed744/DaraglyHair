package com.daragly.daraglyhair.feature.authentication.domain.model

data class AuthToken(
    val token: String,
    val refreshToken: String,
    val refreshTokenExpiryTime: String,
    val userId: String?,
    val roles: List<String>,
    val permissions: List<String>,
)

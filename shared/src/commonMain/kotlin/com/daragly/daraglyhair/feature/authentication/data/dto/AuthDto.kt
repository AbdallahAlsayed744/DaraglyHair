package com.daragly.daraglyhair.feature.authentication.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(
    @SerialName("token")
    val token: String? = null,
    @SerialName("refreshToken")
    val refreshToken: String? = null,
    @SerialName("refreshTokenExpiryTime")
    val refreshTokenExpiryTime: String? = null,
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("roles")
    val roles: List<String> = emptyList(),
    @SerialName("permissions")
    val permissions: List<String> = emptyList(),
)

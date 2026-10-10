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
)

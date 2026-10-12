package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("id")
    val id: String,
    @SerialName("shopId")
    val shopId: String,
    @SerialName("createdAt")
    val createdAt: String? = null,
)

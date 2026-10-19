package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteDto(
    @SerialName("id")
    val id: String,
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("shopId")
    val shopId: String,
    @SerialName("createdOn")
    val createdOn: String? = null,
)

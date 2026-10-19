package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("id")
    val id: String,
    @SerialName("userName")
    val userName: String? = null,
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("isActive")
    val isActive: Boolean? = null,
    @SerialName("emailConfirmed")
    val emailConfirmed: Boolean? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
)

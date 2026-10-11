package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarbershopDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("governorate")
    val governorate: String,
    @SerialName("city")
    val city: String,
    @SerialName("district")
    val district: String? = null,
    @SerialName("streetAddress")
    val streetAddress: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("isVerified")
    val isVerified: Boolean = false,
    @SerialName("isBookingEnabled")
    val isBookingEnabled: Boolean = false,
    @SerialName("isActive")
    val isActive: Boolean = true,
)

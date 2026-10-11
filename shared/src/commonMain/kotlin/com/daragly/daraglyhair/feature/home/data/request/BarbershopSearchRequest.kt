package com.daragly.daraglyhair.feature.home.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarbershopSearchRequest(
    @SerialName("pageNumber")
    val pageNumber: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 10,
    @SerialName("keyword")
    val keyword: String? = null,
    @SerialName("governorate")
    val governorate: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("district")
    val district: String? = null,
    @SerialName("isActive")
    val isActive: Boolean = true,
    @SerialName("isVerified")
    val isVerified: Boolean? = null,
    @SerialName("isBookingEnabled")
    val isBookingEnabled: Boolean? = null,
)

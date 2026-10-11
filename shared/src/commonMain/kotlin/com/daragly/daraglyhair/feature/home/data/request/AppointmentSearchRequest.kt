package com.daragly.daraglyhair.feature.home.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentSearchRequest(
    @SerialName("pageNumber")
    val pageNumber: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 10,
    @SerialName("userId")
    val userId: String? = null,
    @SerialName("shopId")
    val shopId: String? = null,
    @SerialName("shopBarberId")
    val shopBarberId: String? = null,
    @SerialName("status")
    val status: Int? = null,
    @SerialName("fromUtc")
    val fromUtc: String? = null,
    @SerialName("toUtc")
    val toUtc: String? = null,
)

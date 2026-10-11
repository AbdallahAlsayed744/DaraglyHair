package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppointmentDto(
    @SerialName("id")
    val id: String,
    @SerialName("shopId")
    val shopId: String,
    @SerialName("shopBarberId")
    val shopBarberId: String? = null,
    @SerialName("status")
    val status: Int,
    @SerialName("startAtUtc")
    val startAtUtc: String,
    @SerialName("endAtUtc")
    val endAtUtc: String,
    @SerialName("totalPriceEgp")
    val totalPriceEgp: Double,
    @SerialName("depositAmountEgp")
    val depositAmountEgp: Double? = null,
    @SerialName("depositStatus")
    val depositStatus: Int? = null,
    @SerialName("gracePeriodMinutes")
    val gracePeriodMinutes: Int,
)

/**
 * Appointment status enumeration values from API:
 * 1: Pending
 * 2: Confirmed
 * 3: Completed
 * 4: CancelledByCustomer
 * 5: CancelledByShop
 * 6: NoShow
 * 7: Arrived
 */

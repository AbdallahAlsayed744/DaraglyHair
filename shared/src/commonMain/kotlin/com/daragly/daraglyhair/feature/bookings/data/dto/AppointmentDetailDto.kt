package com.daragly.daraglyhair.feature.bookings.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Full appointment DTO returned by GET /api/v1/appointments/{id}.
 * Extends the search result with additional fields: notes, cancelledAtUtc,
 * cancelledByUserId, arrivedAtUtc, depositPaidAtUtc.
 */
@Serializable
data class AppointmentDetailDto(
    @SerialName("id")
    val id: String,
    @SerialName("userId")
    val userId: String? = null,
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
    @SerialName("depositPaidAtUtc")
    val depositPaidAtUtc: String? = null,
    @SerialName("arrivedAtUtc")
    val arrivedAtUtc: String? = null,
    @SerialName("gracePeriodMinutes")
    val gracePeriodMinutes: Int = 0,
    @SerialName("notes")
    val notes: String? = null,
    @SerialName("cancelledAtUtc")
    val cancelledAtUtc: String? = null,
    @SerialName("cancelledByUserId")
    val cancelledByUserId: String? = null,
    @SerialName("createdOn")
    val createdOn: String? = null,
)

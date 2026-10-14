package com.daragly.daraglyhair.feature.bookings.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Body of POST /api/v1/appointments/{id}/cancel.
 * [id] must match the {id} in the URL; [reason] is optional (nullable in the API contract).
 */
@Serializable
data class CancelAppointmentRequest(
    @SerialName("id")
    val id: String,
    @SerialName("reason")
    val reason: String? = null,
)

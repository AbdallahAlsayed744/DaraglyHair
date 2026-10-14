package com.daragly.daraglyhair.feature.bookings.domain.model

/** Request to cancel an appointment, with an optional customer-supplied [reason]. */
data class CancellationRequest(
    val appointmentId: String,
    val reason: String?,
)

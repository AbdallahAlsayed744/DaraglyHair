package com.daragly.daraglyhair.feature.home.domain.model

import kotlinx.datetime.Instant

data class Appointment(
    val id: String,
    val shopId: String,
    val shopBarberId: String?,
    val status: AppointmentStatus,
    val startAt: Instant,
    val endAt: Instant,
    val totalPriceEgp: Double,
    val depositAmountEgp: Double?,
    val depositStatus: Int?,
    val gracePeriodMinutes: Int,
    val shopName: String? = null, // Fetched separately if needed
)

enum class AppointmentStatus(val value: Int) {
    PENDING(1),
    CONFIRMED(2),
    COMPLETED(3),
    CANCELLED_BY_CUSTOMER(4),
    CANCELLED_BY_SHOP(5),
    NO_SHOW(6),
    ARRIVED(7);

    companion object {
        fun fromValue(value: Int): AppointmentStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}

/**
 * Checks if this appointment status is considered "upcoming" (relevant for home screen).
 * Includes Pending, Confirmed, and Arrived statuses.
 */
fun AppointmentStatus.isUpcoming(): Boolean = when (this) {
    AppointmentStatus.PENDING,
    AppointmentStatus.CONFIRMED,
    AppointmentStatus.ARRIVED -> true
    else -> false
}

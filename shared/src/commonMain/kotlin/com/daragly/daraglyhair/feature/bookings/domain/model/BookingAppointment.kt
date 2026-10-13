package com.daragly.daraglyhair.feature.bookings.domain.model

import kotlinx.datetime.Instant

/**
 * Domain model for a booking appointment in the Bookings feature.
 * Richer than the Home Appointment model — includes all API fields.
 */
data class BookingAppointment(
    val id: String,
    val userId: String?,
    val shopId: String,
    val shopBarberId: String?,
    val status: BookingStatus,
    val startAt: Instant,
    val endAt: Instant,
    val totalPriceEgp: Double,
    val depositAmountEgp: Double?,
    val depositStatus: DepositStatus,
    val depositPaidAt: Instant?,
    val arrivedAt: Instant?,
    val gracePeriodMinutes: Int,
    val notes: String?,
    val cancelledAt: Instant?,
    val cancelledByUserId: String?,
    val createdOn: Instant?,
    // Enriched after secondary API calls
    val shopName: String? = null,
)

// ── Booking status ────────────────────────────────────────────────────────────

enum class BookingStatus(val value: Int) {
    PENDING(1),
    CONFIRMED(2),
    COMPLETED(3),
    CANCELLED_BY_CUSTOMER(4),
    CANCELLED_BY_SHOP(5),
    NO_SHOW(6),
    ARRIVED(7);

    companion object {
        fun fromValue(value: Int): BookingStatus =
            entries.firstOrNull { it.value == value } ?: PENDING
    }
}

// ── Deposit status ────────────────────────────────────────────────────────────

enum class DepositStatus(val value: Int) {
    NONE(0),
    PENDING(1),
    PAID(2),
    FORFEITED(3),
    REFUNDED(4);

    companion object {
        fun fromValue(value: Int?): DepositStatus =
            if (value == null) NONE
            else entries.firstOrNull { it.value == value } ?: NONE
    }
}

// ── Tab filter ────────────────────────────────────────────────────────────────

enum class BookingTabFilter {
    UPCOMING,
    COMPLETED,
    CANCELLED,
}

// ── Status helpers ────────────────────────────────────────────────────────────

fun BookingStatus.isUpcoming(): Boolean = when (this) {
    BookingStatus.PENDING,
    BookingStatus.CONFIRMED,
    BookingStatus.ARRIVED -> true
    else -> false
}

fun BookingStatus.isCompleted(): Boolean = this == BookingStatus.COMPLETED

fun BookingStatus.isCancelled(): Boolean = when (this) {
    BookingStatus.CANCELLED_BY_CUSTOMER,
    BookingStatus.CANCELLED_BY_SHOP,
    BookingStatus.NO_SHOW -> true
    else -> false
}

/** Returns true if the appointment is eligible for customer cancellation. */
fun BookingStatus.isCancellable(): Boolean = when (this) {
    BookingStatus.PENDING,
    BookingStatus.CONFIRMED -> true
    else -> false
}

fun BookingStatus.matchesTab(tab: BookingTabFilter): Boolean = when (tab) {
    BookingTabFilter.UPCOMING  -> isUpcoming()
    BookingTabFilter.COMPLETED -> isCompleted()
    BookingTabFilter.CANCELLED -> isCancelled()
}

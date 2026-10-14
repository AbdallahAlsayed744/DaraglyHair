package com.daragly.daraglyhair.feature.shopbooking.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/** Shop ↔ barber relationship. [shopBarberId] is what the booking APIs call `shopBarberId`. */
data class ShopBarber(
    val shopBarberId: String,
    val shopId: String,
    val barberId: String,
    val isActive: Boolean,
    val isBookingEnabled: Boolean,
) {
    val isBookable: Boolean get() = isActive && isBookingEnabled
}

/** Barber profile — the API provides no display name, image, or rating. */
data class BarberProfile(
    val id: String,
    val userId: String?,
    val bio: String?,
    val experienceYears: Int?,
    val specialization: String?,
    val isActive: Boolean,
)

/** Shop ↔ service relationship, enriched with the catalog name when resolvable. */
data class ShopService(
    val shopServiceId: String,
    val shopId: String,
    val serviceId: String,
    val name: String?,
    val priceEgp: Double,
    val durationMinutes: Int,
    val isActive: Boolean,
)

/** A single bookable slot returned by the availability API. Times stay UTC. */
data class BookingSlot(
    val startAt: Instant,
    val endAt: Instant,
    val shopBarberId: String?,
    val isReachable: Boolean,
)

/** Full availability payload for one date. */
data class SlotOffer(
    val shopName: String?,
    val estimatedTravelMinutes: Int?,
    val slots: List<BookingSlot>,
)

/** Parameters for fetching available slots. */
data class SlotQuery(
    val shopId: String,
    val shopBarberId: String?,
    val date: LocalDate,
    val durationMinutes: Int,
)

/** Parameters for creating an appointment. */
data class BookingSubmission(
    val shopId: String,
    val shopBarberId: String?,
    val startAtUtc: Instant,
    val shopServiceIds: List<String>,
    val notes: String?,
)

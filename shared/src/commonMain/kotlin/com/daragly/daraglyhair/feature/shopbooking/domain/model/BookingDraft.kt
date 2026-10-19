package com.daragly.daraglyhair.feature.shopbooking.domain.model

import com.daragly.daraglyhair.common.domain.location.LocationCoordinates
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Mutable booking selections shared across the multi-screen booking flow
 * (services → date/time → review). Changing an upstream selection invalidates
 * the downstream slot selection.
 */
data class BookingDraft(
    val shopId: String? = null,
    val shopName: String? = null,
    val shopBarberId: String? = null,
    val barberId: String? = null,
    val barberSpecialization: String? = null,
    val selectedServices: List<ShopService> = emptyList(),
    val date: LocalDate? = null,
    val slot: BookingSlot? = null,
    val notes: String = "",
    /** Device position used for travel-time slots and the final booking request. */
    val userLocation: LocationCoordinates? = null,
    val transportationMode: TransportationMode? = null,
) {
    val estimatedTotalEgp: Double get() = selectedServices.sumOf { it.priceEgp }
    val estimatedDurationMinutes: Int get() = selectedServices.sumOf { it.durationMinutes }
    val isValidForSubmission: Boolean
        get() = shopId != null && selectedServices.isNotEmpty() && slot != null
}

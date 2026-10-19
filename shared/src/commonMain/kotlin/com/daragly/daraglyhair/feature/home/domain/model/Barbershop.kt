package com.daragly.daraglyhair.feature.home.domain.model

data class Barbershop(
    val id: String,
    val name: String,
    val description: String?,
    val phoneNumber: String,
    val city: String,
    val district: String?,
    val streetAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isVerified: Boolean,
    val isBookingEnabled: Boolean,
    val isActive: Boolean,
    val averageRating: Double? = null,
    val reviewCount: Int? = null,
    val isFavorite: Boolean = false,
    /** Server-provided when the search includes user coordinates; otherwise a client-side estimate. */
    val distanceKm: Double? = null,
)

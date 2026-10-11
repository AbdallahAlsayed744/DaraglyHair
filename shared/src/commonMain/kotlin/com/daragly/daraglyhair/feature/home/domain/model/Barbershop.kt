package com.daragly.daraglyhair.feature.home.domain.model

data class Barbershop(
    val id: String,
    val name: String,
    val description: String?,
    val phoneNumber: String,
    val governorate: String,
    val city: String,
    val district: String?,
    val streetAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isVerified: Boolean,
    val isBookingEnabled: Boolean,
    val isActive: Boolean,
    val isFavorite: Boolean = false,
    val distanceKm: Double? = null, // Client-side calculated distance
)

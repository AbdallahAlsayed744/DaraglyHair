package com.daragly.daraglyhair.feature.home.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BarbershopDto(
    @SerialName("id")
    val id: String,
    @SerialName("ownerUserId")
    val ownerUserId: String? = null,
    @SerialName("name")
    val name: String,
    @SerialName("nameAr")
    val nameAr: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("descriptionAr")
    val descriptionAr: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("email")
    val email: String? = null,
    /** Governorate enum — int value (1..27, Egypt governorates). */
    @SerialName("governorate")
    val governorate: Int? = null,
    @SerialName("city")
    val city: String,
    @SerialName("cityAr")
    val cityAr: String? = null,
    @SerialName("district")
    val district: String? = null,
    @SerialName("districtAr")
    val districtAr: String? = null,
    @SerialName("streetAddress")
    val streetAddress: String? = null,
    @SerialName("streetAddressAr")
    val streetAddressAr: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("amenitiesJson")
    val amenitiesJson: String? = null,
    @SerialName("isVerified")
    val isVerified: Boolean = false,
    @SerialName("isBookingEnabled")
    val isBookingEnabled: Boolean = false,
    @SerialName("isActive")
    val isActive: Boolean = true,
    @SerialName("gracePeriodMinutes")
    val gracePeriodMinutes: Int? = null,
    /** Server-calculated distance from the user's coordinates, present only on geo search. */
    @SerialName("distanceKm")
    val distanceKm: Double? = null,
    @SerialName("averageRating")
    val averageRating: Double? = null,
    @SerialName("reviewCount")
    val reviewCount: Int? = null,
    @SerialName("localizedName")
    val localizedName: String? = null,
    @SerialName("localizedCity")
    val localizedCity: String? = null,
    @SerialName("localizedStreetAddress")
    val localizedStreetAddress: String? = null,
)

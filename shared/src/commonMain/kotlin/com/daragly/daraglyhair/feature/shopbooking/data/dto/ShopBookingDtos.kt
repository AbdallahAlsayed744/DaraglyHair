package com.daragly.daraglyhair.feature.shopbooking.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** GET /api/v1/barbers/shop/{shopId} — shop ↔ barber relationship. `id` is the shopBarberId used by booking APIs. */
@Serializable
data class ShopBarberDto(
    @SerialName("id")                val id: String,
    @SerialName("shopId")            val shopId: String,
    @SerialName("barberId")          val barberId: String,
    @SerialName("isActive")          val isActive: Boolean = false,
    @SerialName("isBookingEnabled")  val isBookingEnabled: Boolean = false,
    @SerialName("joinedAtUtc")       val joinedAtUtc: String? = null,
)

/** GET /api/v1/barbers/{id} — barber profile. Contains NO display name / image / rating. */
@Serializable
data class BarberDto(
    @SerialName("id")                      val id: String,
    @SerialName("userId")                  val userId: String? = null,
    @SerialName("bio")                     val bio: String? = null,
    @SerialName("bioAr")                   val bioAr: String? = null,
    @SerialName("experienceYears")         val experienceYears: Int? = null,
    @SerialName("specialization")          val specialization: String? = null,
    @SerialName("specializationAr")        val specializationAr: String? = null,
    @SerialName("isActive")                val isActive: Boolean = false,
    @SerialName("createdOn")               val createdOn: String? = null,
    @SerialName("localizedBio")            val localizedBio: String? = null,
    @SerialName("localizedSpecialization") val localizedSpecialization: String? = null,
)

/** GET /api/v1/services/shop/{shopId} — shop ↔ service relationship. `id` is the shopServiceId required by the booking API. */
@Serializable
data class ShopServiceDto(
    @SerialName("id")              val id: String,
    @SerialName("shopId")          val shopId: String,
    @SerialName("serviceId")       val serviceId: String,
    @SerialName("priceEgp")        val priceEgp: Double = 0.0,
    @SerialName("durationMinutes") val durationMinutes: Int = 0,
    @SerialName("isActive")        val isActive: Boolean = false,
)

/** POST /api/v1/services/search — service catalog item, used to resolve shop-service names. */
@Serializable
data class ServiceCatalogDto(
    @SerialName("id")                     val id: String,
    @SerialName("categoryId")             val categoryId: String? = null,
    @SerialName("name")                   val name: String? = null,
    @SerialName("nameAr")                 val nameAr: String? = null,
    @SerialName("defaultDurationMinutes") val defaultDurationMinutes: Int = 0,
    @SerialName("isActive")               val isActive: Boolean = false,
    @SerialName("localizedName")          val localizedName: String? = null,
)

/** One entry of POST /api/v1/appointments/available-slots response. */
@Serializable
data class AvailableSlotDto(
    @SerialName("startAtUtc")   val startAtUtc: String,
    @SerialName("endAtUtc")     val endAtUtc: String,
    @SerialName("shopBarberId") val shopBarberId: String? = null,
    @SerialName("isReachable")  val isReachable: Boolean = false,
)

/** Full response envelope of POST /api/v1/appointments/available-slots. */
@Serializable
data class AvailableSlotsResponseDto(
    @SerialName("shopId")                    val shopId: String? = null,
    @SerialName("shopName")                  val shopName: String? = null,
    @SerialName("distanceKm")                val distanceKm: Double? = null,
    @SerialName("estimatedTravelMinutes")    val estimatedTravelMinutes: Int? = null,
    @SerialName("transportationMode")        val transportationMode: Int? = null,
    @SerialName("earliestPossibleArrivalUtc") val earliestPossibleArrivalUtc: String? = null,
    @SerialName("availableSlots")            val availableSlots: List<AvailableSlotDto> = emptyList(),
)

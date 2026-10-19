package com.daragly.daraglyhair.feature.shopbooking.data.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** POST /api/v1/appointments/available-slots body. */
@Serializable
data class AvailableSlotsApiRequest(
    @SerialName("shopId")           val shopId: String,
    @SerialName("shopBarberId")     val shopBarberId: String? = null,
    @SerialName("date")             val date: String,
    @SerialName("durationMinutes")  val durationMinutes: Int,
    @SerialName("userLatitude")     val userLatitude: Double,
    @SerialName("userLongitude")    val userLongitude: Double,
    @SerialName("transportationMode") val transportationMode: Int,
    @SerialName("shopServiceIds")   val shopServiceIds: List<String>? = null,
)

/** POST /api/v1/appointments body — only shopId + shopServiceIds are required by the backend. */
@Serializable
data class CreateAppointmentApiRequest(
    @SerialName("shopId")         val shopId: String,
    @SerialName("shopBarberId")   val shopBarberId: String? = null,
    @SerialName("startAtUtc")     val startAtUtc: String,
    @SerialName("shopServiceIds") val shopServiceIds: List<String>,
    @SerialName("notes")          val notes: String? = null,
    @SerialName("userLatitude")   val userLatitude: Double,
    @SerialName("userLongitude")  val userLongitude: Double,
    @SerialName("transportationMode") val transportationMode: Int,
)

/** POST /api/v1/services/search body — used to resolve catalog names for shop services. */
@Serializable
data class ServiceSearchApiRequest(
    @SerialName("pageNumber") val pageNumber: Int = 1,
    @SerialName("pageSize")   val pageSize: Int = 100,
    @SerialName("categoryId") val categoryId: String? = null,
    @SerialName("isActive")   val isActive: Boolean? = null,
)

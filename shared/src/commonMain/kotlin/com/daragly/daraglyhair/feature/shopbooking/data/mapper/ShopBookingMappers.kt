package com.daragly.daraglyhair.feature.shopbooking.data.mapper

import com.daragly.daraglyhair.feature.shopbooking.data.dto.AvailableSlotDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.AvailableSlotsResponseDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.BarberDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.ServiceCatalogDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.ShopBarberDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.ShopServiceDto
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BarberProfile
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingSlot
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopBarber
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopService
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotOffer
import kotlinx.datetime.Instant

fun ShopBarberDto.toDomain(): ShopBarber = ShopBarber(
    shopBarberId = id,
    shopId = shopId,
    barberId = barberId,
    isActive = isActive,
    isBookingEnabled = isBookingEnabled,
)

fun BarberDto.toDomain(): BarberProfile = BarberProfile(
    id = id,
    userId = userId,
    // localized* already reflect the Accept-Language request header
    bio = localizedBio?.takeIf { it.isNotBlank() } ?: bio,
    experienceYears = experienceYears,
    specialization = localizedSpecialization?.takeIf { it.isNotBlank() } ?: specialization,
    isActive = isActive,
)

fun ShopServiceDto.toDomain(catalogNames: Map<String, ServiceCatalogDto>): ShopService = ShopService(
    shopServiceId = id,
    shopId = shopId,
    serviceId = serviceId,
    name = catalogNames[serviceId]?.let { it.localizedName?.takeIf { n -> n.isNotBlank() } ?: it.name },
    priceEgp = priceEgp,
    durationMinutes = durationMinutes,
    isActive = isActive,
)

private fun AvailableSlotDto.toDomain(): BookingSlot? {
    val start = Instant.parse(startAtUtc)
    val end = runCatching { Instant.parse(endAtUtc) }.getOrDefault(start)
    return BookingSlot(
        startAt = start,
        endAt = end,
        shopBarberId = shopBarberId,
        isReachable = isReachable,
    )
}

fun AvailableSlotsResponseDto.toDomain(): SlotOffer = SlotOffer(
    shopName = shopName,
    estimatedTravelMinutes = estimatedTravelMinutes,
    slots = availableSlots.mapNotNull { runCatching { it.toDomain() }.getOrNull() },
)

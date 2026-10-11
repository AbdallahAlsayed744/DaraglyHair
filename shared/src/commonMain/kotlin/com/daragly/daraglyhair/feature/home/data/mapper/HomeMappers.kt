package com.daragly.daraglyhair.feature.home.data.mapper

import com.daragly.daraglyhair.feature.home.data.dto.AppointmentDto
import com.daragly.daraglyhair.feature.home.data.dto.BarbershopDto
import com.daragly.daraglyhair.feature.home.data.dto.ServiceCategoryDto
import com.daragly.daraglyhair.feature.home.data.dto.UserProfileDto
import com.daragly.daraglyhair.feature.home.domain.model.Appointment
import com.daragly.daraglyhair.feature.home.domain.model.AppointmentStatus
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.home.domain.model.ServiceCategory
import com.daragly.daraglyhair.feature.home.domain.model.UserProfile
import kotlinx.datetime.Instant

fun UserProfileDto.toDomain(): UserProfile = UserProfile(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    imageUrl = imageUrl,
)

fun BarbershopDto.toDomain(isFavorite: Boolean = false, distanceKm: Double? = null): Barbershop =
    Barbershop(
        id = id,
        name = name,
        description = description,
        phoneNumber = phoneNumber,
        governorate = governorate,
        city = city,
        district = district,
        streetAddress = streetAddress,
        latitude = latitude,
        longitude = longitude,
        isVerified = isVerified,
        isBookingEnabled = isBookingEnabled,
        isActive = isActive,
        isFavorite = isFavorite,
        distanceKm = distanceKm,
    )

fun ServiceCategoryDto.toDomain(): ServiceCategory = ServiceCategory(
    id = id,
    name = name,
    description = description,
    sortOrder = sortOrder,
    isActive = isActive,
)

fun AppointmentDto.toDomain(shopName: String? = null): Appointment = Appointment(
    id = id,
    shopId = shopId,
    shopBarberId = shopBarberId,
    status = AppointmentStatus.fromValue(status),
    startAt = Instant.parse(startAtUtc),
    endAt = Instant.parse(endAtUtc),
    totalPriceEgp = totalPriceEgp,
    depositAmountEgp = depositAmountEgp,
    depositStatus = depositStatus,
    gracePeriodMinutes = gracePeriodMinutes,
    shopName = shopName,
)

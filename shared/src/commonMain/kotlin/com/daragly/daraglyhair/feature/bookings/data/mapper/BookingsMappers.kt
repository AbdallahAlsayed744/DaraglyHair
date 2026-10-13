package com.daragly.daraglyhair.feature.bookings.data.mapper

import com.daragly.daraglyhair.feature.bookings.data.dto.AppointmentDetailDto
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingStatus
import com.daragly.daraglyhair.feature.bookings.domain.model.DepositStatus
import com.daragly.daraglyhair.feature.home.data.dto.AppointmentDto
import kotlinx.datetime.Instant

// ── AppointmentDto → BookingAppointment (from search results) ─────────────────

fun AppointmentDto.toBookingDomain(shopName: String? = null): BookingAppointment =
    BookingAppointment(
        id                = id,
        userId            = null,
        shopId            = shopId,
        shopBarberId      = shopBarberId,
        status            = BookingStatus.fromValue(status),
        startAt           = Instant.parse(startAtUtc),
        endAt             = Instant.parse(endAtUtc),
        totalPriceEgp     = totalPriceEgp,
        depositAmountEgp  = depositAmountEgp,
        depositStatus     = DepositStatus.fromValue(depositStatus),
        depositPaidAt     = null,
        arrivedAt         = null,
        gracePeriodMinutes = gracePeriodMinutes,
        notes             = null,
        cancelledAt       = null,
        cancelledByUserId = null,
        createdOn         = null,
        shopName          = shopName,
    )

// ── AppointmentDetailDto → BookingAppointment (from detail endpoint) ──────────

fun AppointmentDetailDto.toBookingDomain(shopName: String? = null): BookingAppointment =
    BookingAppointment(
        id                = id,
        userId            = userId,
        shopId            = shopId,
        shopBarberId      = shopBarberId,
        status            = BookingStatus.fromValue(status),
        startAt           = Instant.parse(startAtUtc),
        endAt             = Instant.parse(endAtUtc),
        totalPriceEgp     = totalPriceEgp,
        depositAmountEgp  = depositAmountEgp,
        depositStatus     = DepositStatus.fromValue(depositStatus),
        depositPaidAt     = depositPaidAtUtc?.let { runCatching { Instant.parse(it) }.getOrNull() },
        arrivedAt         = arrivedAtUtc?.let { runCatching { Instant.parse(it) }.getOrNull() },
        gracePeriodMinutes = gracePeriodMinutes,
        notes             = notes,
        cancelledAt       = cancelledAtUtc?.let { runCatching { Instant.parse(it) }.getOrNull() },
        cancelledByUserId = cancelledByUserId,
        createdOn         = createdOn?.let { runCatching { Instant.parse(it) }.getOrNull() },
        shopName          = shopName,
    )

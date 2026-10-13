package com.daragly.daraglyhair.feature.bookings.ui

import androidx.compose.runtime.Composable
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.date_format_full
import daraglyhair.shared.generated.resources.date_time_short
import daraglyhair.shared.generated.resources.month_1
import daraglyhair.shared.generated.resources.month_10
import daraglyhair.shared.generated.resources.month_11
import daraglyhair.shared.generated.resources.month_12
import daraglyhair.shared.generated.resources.month_2
import daraglyhair.shared.generated.resources.month_3
import daraglyhair.shared.generated.resources.month_4
import daraglyhair.shared.generated.resources.month_5
import daraglyhair.shared.generated.resources.month_6
import daraglyhair.shared.generated.resources.month_7
import daraglyhair.shared.generated.resources.month_8
import daraglyhair.shared.generated.resources.month_9
import daraglyhair.shared.generated.resources.price_format
import daraglyhair.shared.generated.resources.time_am
import daraglyhair.shared.generated.resources.time_format_12h
import daraglyhair.shared.generated.resources.time_pm
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// ── Date / time formatting ────────────────────────────────────────────────────

private val monthResources = listOf(
    Res.string.month_1,
    Res.string.month_2,
    Res.string.month_3,
    Res.string.month_4,
    Res.string.month_5,
    Res.string.month_6,
    Res.string.month_7,
    Res.string.month_8,
    Res.string.month_9,
    Res.string.month_10,
    Res.string.month_11,
    Res.string.month_12,
)

/**
 * Formats an Instant to a compact date string using local time.
 */
@Composable
fun Instant.toBookingDateString(): String {
    val ldt = toLocalDateTime(TimeZone.currentSystemDefault())
    return stringResource(
        Res.string.date_format_full,
        ldt.dayOfMonth,
        stringResource(monthResources[ldt.monthNumber - 1]),
        ldt.year,
    )
}

/**
 * Formats an Instant to a 12-hour time string using local time.
 */
@Composable
fun Instant.toBookingTimeString(): String {
    val ldt = toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = ldt.hour
    val minute = ldt.minute.toString().padStart(2, '0')
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    val amPm = stringResource(if (hour < 12) Res.string.time_am else Res.string.time_pm)
    return stringResource(
        Res.string.time_format_12h,
        displayHour.toString().padStart(2, '0'),
        minute,
        amPm,
    )
}

/**
 * Returns a combined "Date • Time" string.
 */
@Composable
fun formatBookingDateTimeShort(startAt: Instant): String = stringResource(
    Res.string.date_time_short,
    startAt.toBookingDateString(),
    startAt.toBookingTimeString(),
)

// ── Price formatting ──────────────────────────────────────────────────────────

@Composable
fun formatEgp(amount: Double): String {
    val rounded = (amount * 100).toLong()
    val value = if (rounded % 100 == 0L) {
        (rounded / 100).toString()
    } else {
        "%.2f".format(amount)
    }
    return stringResource(Res.string.price_format, value)
}

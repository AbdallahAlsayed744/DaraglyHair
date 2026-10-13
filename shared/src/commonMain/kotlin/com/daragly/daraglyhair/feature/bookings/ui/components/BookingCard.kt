package com.daragly.daraglyhair.feature.bookings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGhost
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingStatus
import com.daragly.daraglyhair.feature.bookings.domain.model.DepositStatus
import com.daragly.daraglyhair.feature.bookings.domain.model.isCancellable
import com.daragly.daraglyhair.feature.bookings.ui.formatBookingDateTimeShort
import com.daragly.daraglyhair.feature.bookings.ui.formatEgp
import com.daragly.daraglyhair.feature.bookings.ui.toBookingDateString
import com.daragly.daraglyhair.feature.bookings.ui.toBookingTimeString
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.barbershop_fallback
import daraglyhair.shared.generated.resources.booking_deposit
import daraglyhair.shared.generated.resources.booking_total
import daraglyhair.shared.generated.resources.cancel_booking
import daraglyhair.shared.generated.resources.deposit_forfeited
import daraglyhair.shared.generated.resources.deposit_paid
import daraglyhair.shared.generated.resources.deposit_pending
import daraglyhair.shared.generated.resources.deposit_refunded
import daraglyhair.shared.generated.resources.view_details
import org.jetbrains.compose.resources.stringResource

/**
 * Premium, compact appointment card used across all three Bookings tabs.
 *
 * Layout:
 * ┌──────────────────────────────────────────┐
 * │  Shop Name (or placeholder)   [Status]   │
 * │  📅 Date • 🕐 Time                       │
 * │  💰 Total   (💳 Deposit info if present) │
 * │  ─────────────────────────────────────   │
 * │  [View Details]        [Cancel?]         │
 * └──────────────────────────────────────────┘
 */
@Composable
fun BookingCard(
    appointment: BookingAppointment,
    onViewDetailsClick: () -> Unit,
    onCancelClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = HallaakTheme.shapes.card,
        colors = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
        ) {

            // ── Row 1: Shop name + Status badge ─────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Store,
                        contentDescription = null,
                        tint = HallaakTheme.colors.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = appointment.shopName ?: stringResource(Res.string.barbershop_fallback),
                        style = HallaakTheme.typography.title.extraLarge,
                        color = HallaakTheme.colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }

                Spacer(Modifier.width(HallaakTheme.dimensions.space8))

                BookingStatusBadge(status = appointment.status)
            }

            Spacer(Modifier.height(HallaakTheme.dimensions.space12))

            // ── Row 2: Date & Time ───────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = HallaakTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = appointment.startAt.toBookingDateString(),
                        style = HallaakTheme.typography.body.small,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space4),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = HallaakTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = appointment.startAt.toBookingTimeString(),
                        style = HallaakTheme.typography.body.small,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(HallaakTheme.dimensions.space8))

            // ── Row 3: Price info ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
            ) {
                Text(
                    text = stringResource(
                        Res.string.booking_total,
                        formatEgp(appointment.totalPriceEgp),
                    ),
                    style = HallaakTheme.typography.label.large,
                    color = HallaakTheme.colors.primary,
                )

                // Deposit info — only when a deposit amount is present
                appointment.depositAmountEgp?.let { depositAmount ->
                    if (depositAmount > 0) {
                        val depositLabel = when (appointment.depositStatus) {
                            DepositStatus.PAID      -> stringResource(Res.string.deposit_paid)
                            DepositStatus.PENDING   -> stringResource(Res.string.deposit_pending)
                            DepositStatus.FORFEITED -> stringResource(Res.string.deposit_forfeited)
                            DepositStatus.REFUNDED  -> stringResource(Res.string.deposit_refunded)
                            DepositStatus.NONE      -> return@let
                        }
                        Text(
                            text = stringResource(
                                Res.string.booking_deposit,
                                formatEgp(depositAmount),
                                depositLabel,
                            ),
                            style = HallaakTheme.typography.label.large,
                            color = HallaakTheme.colors.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(HallaakTheme.dimensions.space12))
            HorizontalDivider(color = HallaakTheme.colors.stroke)
            Spacer(Modifier.height(HallaakTheme.dimensions.space4))

            // ── Row 4: Actions ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onViewDetailsClick) {
                    Text(
                        text = stringResource(Res.string.view_details),
                        style = HallaakTheme.typography.label.large,
                        color = HallaakTheme.colors.primary,
                    )
                }

                if (onCancelClick != null) {
                    TextButton(onClick = onCancelClick) {
                        Text(
                            text = stringResource(Res.string.cancel_booking),
                            style = HallaakTheme.typography.label.large,
                            color = HallaakTheme.colors.status.error,
                        )
                    }
                }
            }
        }
    }
}

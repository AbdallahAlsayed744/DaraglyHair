package com.daragly.daraglyhair.designsystem.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.appointment_status_cancelled
import daraglyhair.shared.generated.resources.appointment_status_completed
import daraglyhair.shared.generated.resources.appointment_status_upcoming
import daraglyhair.shared.generated.resources.status_pending
import org.jetbrains.compose.resources.stringResource

// ─────────────────────────────────────────────
//  HallaakAppointmentCard
//
//  Appointment history / upcoming booking card.
//
//  Layout:
//    ┌────────────────────────────────────────┐
//    │ 🗓 [Date]        [Status Badge]         │
//    │ Shop Name                               │
//    │ Service · Barber                        │
//    └────────────────────────────────────────┘
// ─────────────────────────────────────────────

enum class HallaakAppointmentStatus {
    UPCOMING, COMPLETED, CANCELLED, PENDING
}

@Composable
fun HallaakAppointmentCard(
    shopName: String,
    serviceName: String,
    barberName: String,
    dateTime: String,
    status: HallaakAppointmentStatus,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = HallaakTheme.shapes.card,
        colors    = CardDefaults.cardColors(
            containerColor = HallaakTheme.colors.background.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick   = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Top row: date + status
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        modifier           = Modifier.size(16.dp),
                        imageVector        = Icons.Outlined.CalendarMonth,
                        tint               = HallaakTheme.colors.onSurfaceVariant,
                        contentDescription = null,
                    )
                    Text(
                        text  = dateTime,
                        style = HallaakTheme.typography.label.large,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                }

                HallaakStatusChip(status = status)
            }

            // Shop name
            Text(
                text  = shopName,
                style = HallaakTheme.typography.title.extraLarge,
                color = HallaakTheme.colors.onSurface,
            )

            // Service · Barber
            Text(
                text  = "$serviceName · $barberName",
                style = HallaakTheme.typography.body.small,
                color = HallaakTheme.colors.onSurfaceVariant,
            )
        }
    }
}

// ── Status chip ───────────────────────────────

@Composable
fun HallaakStatusChip(
    status: HallaakAppointmentStatus,
    modifier: Modifier = Modifier,
) {
    val (label, bgColor) = when (status) {
        HallaakAppointmentStatus.UPCOMING   -> stringResource(Res.string.appointment_status_upcoming) to HallaakTheme.colors.status.info
        HallaakAppointmentStatus.COMPLETED  -> stringResource(Res.string.appointment_status_completed) to HallaakTheme.colors.status.success
        HallaakAppointmentStatus.CANCELLED  -> stringResource(Res.string.appointment_status_cancelled) to HallaakTheme.colors.status.error
        HallaakAppointmentStatus.PENDING    -> stringResource(Res.string.status_pending) to HallaakTheme.colors.status.pending
    }

    Box(
        modifier = modifier
            .background(color = bgColor.copy(alpha = 0.15f), shape = HallaakTheme.shapes.pill)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text  = label,
            style = HallaakTheme.typography.label.medium,
            color = bgColor,
        )
    }
}

package com.daragly.daraglyhair.feature.bookings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingStatus
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.status_arrived
import daraglyhair.shared.generated.resources.status_cancelled_by_customer
import daraglyhair.shared.generated.resources.status_cancelled_by_shop
import daraglyhair.shared.generated.resources.status_completed
import daraglyhair.shared.generated.resources.status_confirmed
import daraglyhair.shared.generated.resources.status_no_show
import daraglyhair.shared.generated.resources.status_pending
import org.jetbrains.compose.resources.stringResource

/**
 * Compact status badge shown on appointment cards and detail screens.
 * Uses semantic colors from [HallaakTheme.colors.status].
 */
@Composable
fun BookingStatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier,
) {
    val (label, color) = statusLabelAndColor(status)

    Text(
        text = label,
        style = HallaakTheme.typography.label.medium,
        color = color,
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = HallaakTheme.shapes.pill,
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
fun statusLabelAndColor(status: BookingStatus): Pair<String, Color> = when (status) {
    BookingStatus.PENDING             -> stringResource(Res.string.status_pending) to HallaakTheme.colors.status.pending
    BookingStatus.CONFIRMED           -> stringResource(Res.string.status_confirmed) to HallaakTheme.colors.status.info
    BookingStatus.ARRIVED             -> stringResource(Res.string.status_arrived) to HallaakTheme.colors.status.success
    BookingStatus.COMPLETED           -> stringResource(Res.string.status_completed) to HallaakTheme.colors.status.success
    BookingStatus.CANCELLED_BY_CUSTOMER -> stringResource(Res.string.status_cancelled_by_customer) to HallaakTheme.colors.status.error
    BookingStatus.CANCELLED_BY_SHOP   -> stringResource(Res.string.status_cancelled_by_shop) to HallaakTheme.colors.status.error
    BookingStatus.NO_SHOW             -> stringResource(Res.string.status_no_show) to HallaakTheme.colors.onSurfaceVariant
}

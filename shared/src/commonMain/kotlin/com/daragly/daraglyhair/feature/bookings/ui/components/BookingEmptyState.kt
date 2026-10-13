package com.daragly.daraglyhair.feature.bookings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingTabFilter
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.bookings_empty_cancelled_subtitle
import daraglyhair.shared.generated.resources.bookings_empty_cancelled_title
import daraglyhair.shared.generated.resources.bookings_empty_completed_subtitle
import daraglyhair.shared.generated.resources.bookings_empty_completed_title
import daraglyhair.shared.generated.resources.bookings_empty_upcoming_subtitle
import daraglyhair.shared.generated.resources.bookings_empty_upcoming_title
import daraglyhair.shared.generated.resources.explore_barbershops
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Tab-specific empty states (Screens A/F/G).
 */
@Composable
fun BookingEmptyState(
    tab: BookingTabFilter,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (icon, titleRes, subtitleRes, showCta) = when (tab) {
        BookingTabFilter.UPCOMING -> EmptyStateData(
            icon = Icons.Outlined.CalendarMonth,
            titleRes = Res.string.bookings_empty_upcoming_title,
            subtitleRes = Res.string.bookings_empty_upcoming_subtitle,
            showCta = true,
        )
        BookingTabFilter.COMPLETED -> EmptyStateData(
            icon = Icons.Outlined.CheckCircle,
            titleRes = Res.string.bookings_empty_completed_title,
            subtitleRes = Res.string.bookings_empty_completed_subtitle,
            showCta = false,
        )
        BookingTabFilter.CANCELLED -> EmptyStateData(
            icon = Icons.Outlined.EventBusy,
            titleRes = Res.string.bookings_empty_cancelled_title,
            subtitleRes = Res.string.bookings_empty_cancelled_subtitle,
            showCta = false,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space32)
            .padding(top = HallaakTheme.dimensions.space64, bottom = HallaakTheme.dimensions.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HallaakTheme.colors.gold.copy(alpha = 0.55f),
            modifier = Modifier.size(96.dp),
        )

        Text(
            text = stringResource(titleRes),
            style = HallaakTheme.typography.headline.extraSmall,
            color = HallaakTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(subtitleRes),
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        if (showCta) {
            Spacer(Modifier.height(HallaakTheme.dimensions.space8))
            HallaakButtonPrimary(
                label = stringResource(Res.string.explore_barbershops),
                onClick = onExploreClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private data class EmptyStateData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val titleRes: StringResource,
    val subtitleRes: StringResource,
    val showCta: Boolean,
)

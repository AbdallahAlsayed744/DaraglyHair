package com.daragly.daraglyhair.feature.bookings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonSecondary
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.bookings_error_subtitle
import daraglyhair.shared.generated.resources.bookings_error_title
import daraglyhair.shared.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

/**
 * Screen I — Error state shown when bookings cannot be loaded.
 * Clearly differentiates from an empty state to avoid misleading the user.
 */
@Composable
fun BookingErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HallaakTheme.dimensions.space32)
            .padding(top = HallaakTheme.dimensions.space64, bottom = HallaakTheme.dimensions.space32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space12),
    ) {
        Icon(
            imageVector = Icons.Outlined.WifiOff,
            contentDescription = null,
            tint = HallaakTheme.colors.status.error.copy(alpha = 0.45f),
            modifier = Modifier.size(80.dp),
        )

        Spacer(Modifier.height(HallaakTheme.dimensions.space4))

        Text(
            text = stringResource(Res.string.bookings_error_title),
            style = HallaakTheme.typography.headline.extraSmall,
            color = HallaakTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(Res.string.bookings_error_subtitle),
            style = HallaakTheme.typography.body.medium,
            color = HallaakTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(HallaakTheme.dimensions.space8))

        HallaakButtonPrimary(
            label = stringResource(Res.string.retry),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

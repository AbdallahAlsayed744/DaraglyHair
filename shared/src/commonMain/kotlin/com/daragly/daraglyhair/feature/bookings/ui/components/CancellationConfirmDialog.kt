package com.daragly.daraglyhair.feature.bookings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonPrimary
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.cancel_booking
import daraglyhair.shared.generated.resources.cancel_booking_body
import daraglyhair.shared.generated.resources.cancel_booking_confirm
import daraglyhair.shared.generated.resources.cancel_booking_dismiss
import org.jetbrains.compose.resources.stringResource

/**
 * Screen E — Cancellation confirmation dialog.
 *
 * Does NOT promise a refund. Describes the action clearly.
 */
@Composable
fun CancellationConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = HallaakTheme.shapes.dialog,
            colors = CardDefaults.cardColors(
                containerColor = HallaakTheme.colors.background.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HallaakTheme.dimensions.space24),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Warning icon
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = HallaakTheme.colors.status.warning,
                    modifier = Modifier.size(48.dp),
                )

                Spacer(Modifier.height(HallaakTheme.dimensions.space16))

                Text(
                    text = stringResource(Res.string.cancel_booking),
                    style = HallaakTheme.typography.headline.small,
                    color = HallaakTheme.colors.onSurface,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(HallaakTheme.dimensions.space8))

                Text(
                    text = stringResource(Res.string.cancel_booking_body),
                    style = HallaakTheme.typography.body.small,
                    color = HallaakTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(HallaakTheme.dimensions.space24))

                // Actions
                HallaakButtonPrimary(
                    label = stringResource(Res.string.cancel_booking_confirm),
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(HallaakTheme.dimensions.space8))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.cancel_booking_dismiss),
                        style = HallaakTheme.typography.body.medium,
                        color = HallaakTheme.colors.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

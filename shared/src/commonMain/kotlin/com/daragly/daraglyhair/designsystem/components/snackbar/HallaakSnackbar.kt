package com.daragly.daraglyhair.designsystem.components.snackbar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakSnackbar
//
//  Typed snackbar for success / error / info / warning.
//  16dp rounded, 1.5dp border, icon + body message.
// ─────────────────────────────────────────────

enum class HallaakSnackbarType { SUCCESS, ERROR, WARNING, INFO }

@Composable
fun HallaakSnackbar(
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    type: HallaakSnackbarType = HallaakSnackbarType.INFO,
    colors: HallaakSnackbarColors = HallaakSnackbarDefaults.colors(type),
) {
    val shape = RoundedCornerShape(16.dp)

    Snackbar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.5.dp, color = colors.borderColor, shape = shape),
        shape          = shape,
        containerColor = colors.containerColor,
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                modifier           = Modifier.size(22.dp),
                imageVector        = icon,
                tint               = colors.iconColor,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.weight(1f),
                text     = message,
                style    = HallaakTheme.typography.body.small,
                color    = HallaakTheme.colors.onSurface,
            )
        }
    }
}

// ── Color model ───────────────────────────────

data class HallaakSnackbarColors(
    val containerColor: Color,
    val borderColor: Color,
    val iconColor: Color,
)

object HallaakSnackbarDefaults {
    @Composable
    fun colors(type: HallaakSnackbarType): HallaakSnackbarColors = when (type) {
        HallaakSnackbarType.SUCCESS -> HallaakSnackbarColors(
            containerColor = HallaakTheme.colors.background.surfaceHigh,
            borderColor    = HallaakTheme.colors.status.success,
            iconColor      = HallaakTheme.colors.status.success,
        )
        HallaakSnackbarType.ERROR -> HallaakSnackbarColors(
            containerColor = HallaakTheme.colors.background.surfaceHigh,
            borderColor    = HallaakTheme.colors.status.error,
            iconColor      = HallaakTheme.colors.status.error,
        )
        HallaakSnackbarType.WARNING -> HallaakSnackbarColors(
            containerColor = HallaakTheme.colors.background.surfaceHigh,
            borderColor    = HallaakTheme.colors.status.warning,
            iconColor      = HallaakTheme.colors.status.warning,
        )
        HallaakSnackbarType.INFO -> HallaakSnackbarColors(
            containerColor = HallaakTheme.colors.background.surfaceHigh,
            borderColor    = HallaakTheme.colors.status.info,
            iconColor      = HallaakTheme.colors.status.info,
        )
    }
}

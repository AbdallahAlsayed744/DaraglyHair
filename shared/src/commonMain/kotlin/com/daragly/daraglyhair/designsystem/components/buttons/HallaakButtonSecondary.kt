package com.daragly.daraglyhair.designsystem.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakButtonSecondary
//
//  Outlined variant – transparent fill with a
//  primary-blue border and text.
//  Used for secondary actions alongside a primary.
// ─────────────────────────────────────────────

@Composable
fun HallaakButtonSecondary(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    colors: HallaakButtonColors = HallaakButtonSecondaryDefaults.colors,
) {
    HallaakButton(
        label     = label,
        onClick   = onClick,
        modifier  = modifier,
        colors    = colors,
        isEnabled = isEnabled,
        labelStyle = HallaakButtonSecondaryDefaults.labelStyle,
    )
}

object HallaakButtonSecondaryDefaults {
    val labelStyle
        @Composable get() = HallaakTheme.typography.body.medium

    val colors: HallaakButtonColors
        @Composable get() = HallaakButtonDefaults.color(
            containerColor         = HallaakTheme.colors.background.surfaceHigh,
            contentColor           = HallaakTheme.colors.primary,
            disabledContainerColor = HallaakTheme.colors.background.disabled,
            disabledContentColor   = HallaakTheme.colors.onSurfaceVariant,
            borderColor            = HallaakTheme.colors.primary,
        )
}

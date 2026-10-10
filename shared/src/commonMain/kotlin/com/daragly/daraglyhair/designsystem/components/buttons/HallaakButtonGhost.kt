package com.daragly.daraglyhair.designsystem.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakButtonGhost
//
//  Completely borderless / text-only variant.
//  Used for: "Skip", "Cancel", "View all" links.
//  No background, no border – just colored label.
// ─────────────────────────────────────────────

@Composable
fun HallaakButtonGhost(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    HallaakButton(
        label     = label,
        onClick   = onClick,
        modifier  = modifier,
        isEnabled = isEnabled,
        colors    = HallaakButtonGhostDefaults.colors,
        labelStyle = HallaakButtonGhostDefaults.labelStyle,
    )
}

object HallaakButtonGhostDefaults {
    val labelStyle
        @Composable get() = HallaakTheme.typography.body.medium

    val colors: HallaakButtonColors
        @Composable get() = HallaakButtonDefaults.color(
            containerColor         = Color.Transparent,
            contentColor           = HallaakTheme.colors.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor   = HallaakTheme.colors.onSurfaceVariant,
            borderColor            = Color.Transparent,
        )
}

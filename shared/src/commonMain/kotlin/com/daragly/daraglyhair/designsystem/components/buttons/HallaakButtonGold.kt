package com.daragly.daraglyhair.designsystem.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakButtonGold
//
//  Premium / accent variant – warm gold fill,
//  dark charcoal text.
//
//  Use for: "Book Now", promotional CTAs,
//  upgrade prompts – wherever a touch of luxury
//  should catch the eye.
// ─────────────────────────────────────────────

@Composable
fun HallaakButtonGold(
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
        colors    = HallaakButtonGoldDefaults.colors,
    )
}

object HallaakButtonGoldDefaults {
    val colors: HallaakButtonColors
        @Composable get() = HallaakButtonDefaults.color(
            containerColor         = HallaakTheme.colors.gold,
            contentColor           = HallaakTheme.colors.onSurface,
            disabledContainerColor = HallaakTheme.colors.background.disabled,
            disabledContentColor   = HallaakTheme.colors.onSurfaceVariant,
            borderColor            = HallaakTheme.colors.gold,
        )
}

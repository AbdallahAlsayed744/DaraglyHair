package com.daragly.daraglyhair.designsystem.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  HallaakButtonPrimary
//
//  Filled Deep-Slate-Blue button – the main CTA
//  used on login, booking, confirmation actions.
//
//  Visual: solid primary-blue fill, cream/white
//  text, slightly rounded (14dp) corners.
// ─────────────────────────────────────────────

@Composable
fun HallaakButtonPrimary(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    shape: Shape = HallaakTheme.shapes.button,
) {
    HallaakButton(
        modifier  = modifier,
        label     = label,
        onClick   = onClick,
        shape     = shape,
        isEnabled = isEnabled,
        colors    = HallaakButtonPrimaryDefaults.colors,
    )
}

object HallaakButtonPrimaryDefaults {
    val colors: HallaakButtonColors
        @Composable get() = HallaakButtonDefaults.color(
            containerColor         = HallaakTheme.colors.primary,
            contentColor           = HallaakTheme.colors.background.surfaceHigh,
            disabledContainerColor = HallaakTheme.colors.background.disabled,
            disabledContentColor   = HallaakTheme.colors.onSurfaceVariant,
            borderColor            = HallaakTheme.colors.primary,
        )
}

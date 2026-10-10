package com.daragly.daraglyhair.designsystem.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

// ─────────────────────────────────────────────
//  Modifier Extensions
//
//  RTL-aware utilities matching Delivery-User's
//  autoMirror + clickableWithNoRipple patterns.
// ─────────────────────────────────────────────

/**
 * Automatically flips the composable horizontally in RTL layouts.
 * Ideal for directional icons (arrows, chevrons).
 */
@Composable
fun Modifier.autoMirror(): Modifier {
    val layoutDirection = LocalLayoutDirection.current
    return when (layoutDirection) {
        LayoutDirection.Ltr -> this
        LayoutDirection.Rtl -> this.scale(scaleX = -1f, scaleY = 1f)
    }
}

/**
 * Click handler with no ripple effect.
 * Used for custom interactive elements that manage their own visual feedback.
 */
@Composable
fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this.clickable(
        onClick           = onClick,
        interactionSource = interactionSource,
        indication        = null,
        enabled           = enabled,
    )
}

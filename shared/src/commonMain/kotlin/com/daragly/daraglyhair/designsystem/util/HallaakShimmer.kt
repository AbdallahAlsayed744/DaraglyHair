package com.daragly.daraglyhair.designsystem.util

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme

// ─────────────────────────────────────────────
//  Shimmer utility
//
//  Diagonal sweep shimmer for skeleton loading.
//  Uses surfaceHigh + goldVariant for a warm
//  barbershop-feel rather than cold gray.
// ─────────────────────────────────────────────

@Composable
fun rememberHallaakShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "hallaak-shimmer")
    val translate by transition.animateFloat(
        initialValue   = 0f,
        targetValue    = 1200f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer-translate",
    )

    val base    = HallaakTheme.colors.background.surface
    val gleam   = HallaakTheme.colors.goldVariant.copy(alpha = 0.35f)

    return Brush.linearGradient(
        colors = listOf(
            base.copy(alpha = 0.9f),
            gleam,
            base.copy(alpha = 0.9f),
        ),
        start = Offset(translate - 500f, translate - 500f),
        end   = Offset(translate, translate),
    )
}

/** Drop-in shimmer placeholder Box */
@Composable
fun HallaakShimmerBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(rememberHallaakShimmerBrush()))
}

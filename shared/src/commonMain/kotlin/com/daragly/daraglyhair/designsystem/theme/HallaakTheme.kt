package com.daragly.daraglyhair.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

// ─────────────────────────────────────────────
//  HallaakTheme – composition root
//
//  buildHallaakTypography() is called here so the
//  Cairo FontFamily is loaded once per theme
//  composition, then provided via CompositionLocal
//  to all children.
// ─────────────────────────────────────────────

@Composable
fun HallaakTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors     = if (isDarkTheme) hallaakDarkColors else hallaakLightColors
    val typography = buildHallaakTypography()   // Cairo loaded from composeResources/font/

    CompositionLocalProvider(
        LocalHallaakColors     provides colors,
        localHallaakTypography provides typography,
        localHallaakShapes     provides hallaakShapes,
        localHallaakDimensions provides hallaakDimensions,
        content = content,
    )
}

// ── Theme accessor object ─────────────────────

object HallaakTheme {
    val colors: HallaakColors
        @Composable @ReadOnlyComposable
        get() = LocalHallaakColors.current

    val typography: HallaakTypography
        @Composable @ReadOnlyComposable
        get() = localHallaakTypography.current

    val shapes: HallaakShapes
        @Composable @ReadOnlyComposable
        get() = localHallaakShapes.current

    val dimensions: HallaakDimensions
        @Composable @ReadOnlyComposable
        get() = localHallaakDimensions.current
}

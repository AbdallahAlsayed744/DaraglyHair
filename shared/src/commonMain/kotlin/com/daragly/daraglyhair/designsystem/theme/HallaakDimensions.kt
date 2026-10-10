package com.daragly.daraglyhair.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────
//  Halla'ak Spacing / Dimension Tokens
//
//  A single spacing scale + named semantic slots
//  keeps padding consistent across all screens.
// ─────────────────────────────────────────────

val localHallaakDimensions = staticCompositionLocalOf<HallaakDimensions> {
    error("HallaakDimensions not provided")
}

data class HallaakDimensions(
    // ── Base spacing scale ───────────────────
    val space2: Dp,    // 2dp  – hair-line gaps
    val space4: Dp,    // 4dp  – icon badge offsets
    val space8: Dp,    // 8dp  – inner padding
    val space12: Dp,   // 12dp
    val space16: Dp,   // 16dp – horizontal screen margin
    val space20: Dp,   // 20dp
    val space24: Dp,   // 24dp
    val space32: Dp,   // 32dp
    val space40: Dp,   // 40dp
    val space48: Dp,   // 48dp
    val space56: Dp,   // 56dp
    val space64: Dp,   // 64dp

    // ── Component sizes ──────────────────────
    val topBarHeight: Dp,
    val bottomNavHeight: Dp,
    val buttonHeight: Dp,
    val inputHeight: Dp,
    val iconSize: Dp,
    val iconSizeLarge: Dp,
    val avatarSmall: Dp,
    val avatarMedium: Dp,
    val avatarLarge: Dp,
    val fabSize: Dp,
)

val hallaakDimensions = HallaakDimensions(
    space2  = 2.dp,
    space4  = 4.dp,
    space8  = 8.dp,
    space12 = 12.dp,
    space16 = 16.dp,
    space20 = 20.dp,
    space24 = 24.dp,
    space32 = 32.dp,
    space40 = 40.dp,
    space48 = 48.dp,
    space56 = 56.dp,
    space64 = 64.dp,

    topBarHeight      = 56.dp,
    bottomNavHeight   = 72.dp,
    buttonHeight      = 52.dp,
    inputHeight       = 52.dp,
    iconSize          = 24.dp,
    iconSizeLarge     = 32.dp,
    avatarSmall       = 36.dp,
    avatarMedium      = 52.dp,
    avatarLarge       = 80.dp,
    fabSize           = 60.dp,
)

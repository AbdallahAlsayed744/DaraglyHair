package com.daragly.daraglyhair.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────
//  Halla'ak Shape Tokens
//
//  Strategy: slightly more rounded than a typical
//  utilitarian app to feel modern and polished,
//  while staying crisp and trustworthy.
// ─────────────────────────────────────────────

val localHallaakShapes = staticCompositionLocalOf<HallaakShapes> {
    error("HallaakShapes not provided")
}

data class HallaakShapes(
    /** Chips, tags, badge pills */
    val pill: RoundedCornerShape,
    /** Primary buttons */
    val button: RoundedCornerShape,
    /** Cards, bottom-sheets header */
    val card: RoundedCornerShape,
    /** Text fields */
    val input: RoundedCornerShape,
    /** Dialogs, modals */
    val dialog: RoundedCornerShape,
    /** Small badges / indicator dots */
    val badge: RoundedCornerShape,
    /** Avatar / profile images */
    val avatar: RoundedCornerShape,
    /** Bottom navigation bar */
    val bottomNav: RoundedCornerShape,
)

val hallaakShapes = HallaakShapes(
    pill      = RoundedCornerShape(50.dp),
    button    = RoundedCornerShape(14.dp),
    card      = RoundedCornerShape(16.dp),
    input     = RoundedCornerShape(12.dp),
    dialog    = RoundedCornerShape(24.dp),
    badge     = RoundedCornerShape(50.dp),
    avatar    = RoundedCornerShape(50.dp),
    bottomNav = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
)

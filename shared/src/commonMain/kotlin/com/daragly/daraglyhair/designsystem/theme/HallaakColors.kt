package com.daragly.daraglyhair.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
//  Halla'ak (حلاقك) – Design-System Color Tokens
//
//  Brand identity: Barbershop / Grooming
//  Palette direction:
//    • Deep Slate Blue  → authority, trust, premium
//    • Warm Gold/Amber  → craft, warmth, Egyptian feel
//    • Cream/Off-White  → cleanliness, light surfaces
//    • Dark Charcoal    → sharp, masculine contrast
// ─────────────────────────────────────────────

val LocalHallaakColors = staticCompositionLocalOf<HallaakColors> {
    error("HallaakColors not provided – wrap with HallaakTheme {}")
}

// ── Token model ───────────────────────────────

data class HallaakColors(
    /** Deep Slate Blue – primary brand, CTAs, active elements */
    val primary: Color,
    /** Warm Gold – accents, ratings, highlights */
    val gold: Color,
    /** Soft Gold variant – tags, badges, subtle fills */
    val goldVariant: Color,
    /** Charcoal – primary text, strong contrast */
    val onSurface: Color,
    /** Medium Gray – secondary text, inactive labels */
    val onSurfaceVariant: Color,
    /** Background surfaces */
    val background: HallaakBackgroundColors,
    /** Semantic status colors */
    val status: HallaakStatusColors,
    /** Dividers, strokes, borders */
    val stroke: Color,
    /** Shadow / elevation tint */
    val shadow: Color,
)

data class HallaakBackgroundColors(
    /** Page / screen background */
    val canvas: Color,
    /** Cards, sheets, elevated surfaces */
    val surface: Color,
    /** Slightly elevated – input fill, dialog bg */
    val surfaceHigh: Color,
    /** Disabled states */
    val disabled: Color,
    /** Hint / placeholder */
    val hint: Color,
    /** Overlay scrim */
    val scrim: Color,
)

data class HallaakStatusColors(
    val success: Color,
    val warning: Color,
    val error: Color,
    val info: Color,
    /** Pending/awaiting tint */
    val pending: Color,
)

// ── Light palette ─────────────────────────────

private val lightBackground = HallaakBackgroundColors(
    canvas     = Color(0xFFF6F4F0),   // warm cream – barbershop walls
    surface    = Color(0xFFFFFFFF),
    surfaceHigh = Color(0xFFFAF9F7),
    disabled   = Color(0xFFE8E6E1),
    hint       = Color(0x994A4A4A),
    scrim      = Color(0x80000000),
)

private val lightStatus = HallaakStatusColors(
    success = Color(0xFF2E9E5B),   // confident green
    warning = Color(0xFFE59B1A),   // warm amber
    error   = Color(0xFFD93025),   // clear red
    info    = Color(0xFF1A73E8),   // standard blue
    pending = Color(0xFF8B5CF6),   // soft violet
)

val hallaakLightColors = HallaakColors(
    primary          = Color(0xFF1C3557),   // Deep Slate Blue
    gold             = Color(0xFFC8952A),   // Warm Gold
    goldVariant      = Color(0xFFF5D98F),   // Soft Gold
    onSurface        = Color(0xFF1A1A1A),   // Near-black charcoal
    onSurfaceVariant = Color(0xFF5C5C5C),   // Medium gray
    background       = lightBackground,
    status           = lightStatus,
    stroke           = Color(0xFFDDDAD3),
    shadow           = Color(0x14000000),
)

// ── Dark palette ──────────────────────────────

private val darkBackground = HallaakBackgroundColors(
    canvas     = Color(0xFF0F1923),   // deep navy-dark
    surface    = Color(0xFF1B2636),   // card surface
    surfaceHigh = Color(0xFF243040),  // elevated inputs / dialogs
    disabled   = Color(0xFF2A3040),
    hint       = Color(0x99C0BDB7),
    scrim      = Color(0x99000000),
)

private val darkStatus = HallaakStatusColors(
    success = Color(0xFF34C471),
    warning = Color(0xFFFFB930),
    error   = Color(0xFFFF5252),
    info    = Color(0xFF4DA3FF),
    pending = Color(0xFFA78BFA),
)

val hallaakDarkColors = HallaakColors(
    primary          = Color(0xFF4A80B5),   // lighter blue for dark bg
    gold             = Color(0xFFD4A83A),   // bright gold on dark
    goldVariant      = Color(0xFF6B4F1A),   // muted gold fill
    onSurface        = Color(0xFFF0EDE8),   // warm white text
    onSurfaceVariant = Color(0xFFB0ADA6),   // muted secondary text
    background       = darkBackground,
    status           = darkStatus,
    stroke           = Color(0xFF2C3A4A),
    shadow           = Color(0x33000000),
)

package com.daragly.daraglyhair.common.ui.language

import androidx.compose.runtime.Composable

/**
 * Platform-specific language change observer.
 * Mirrors Delivery-User's ObserveLanguageEvent.
 */
@Composable
expect fun ObserveLanguageEvent()

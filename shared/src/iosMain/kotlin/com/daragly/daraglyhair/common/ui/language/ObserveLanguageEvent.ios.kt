package com.daragly.daraglyhair.common.ui.language

import androidx.compose.runtime.Composable

/**
 * iOS implementation of language change observer.
 * iOS handles locale changes through system settings, so this is a no-op.
 */
@Composable
actual fun ObserveLanguageEvent() {
    // iOS handles language changes through system settings automatically
    // No manual locale switching needed like Android
}

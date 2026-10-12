package com.daragly.daraglyhair.common.ui.theme

import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme

/**
 * Global theme-change event.
 * Emitted by SettingsViewModel, observed in HallaakApp
 * to flip isDarkTheme immediately — no restart required.
 */
sealed interface IThemeEvent {
    data class ChangeTheme(val theme: AppTheme) : IThemeEvent
}

package com.daragly.daraglyhair.common.ui.language

/**
 * Language change events for the application.
 * Mirrors Delivery-User's ILanguageEvent.
 */
sealed interface ILanguageEvent {
    val languageCode: String

    data class ChangeLanguage(override val languageCode: String) : ILanguageEvent
}

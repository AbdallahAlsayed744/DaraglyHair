package com.daragly.daraglyhair.feature.settings.domain.model

/**
 * User-selected theme preference.
 * Stored as its [value] string in DataStore.
 */
enum class AppTheme(val value: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromValue(value: String): AppTheme =
            entries.firstOrNull { it.value == value } ?: SYSTEM
    }
}

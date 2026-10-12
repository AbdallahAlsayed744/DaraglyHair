package com.daragly.daraglyhair.feature.settings.data.repository

import com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceEnum
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme
import com.daragly.daraglyhair.feature.settings.domain.repository.ISettingsRepository

class SettingsRepository(
    private val local: ILocalDataSourceProvider,
) : ISettingsRepository {

    // ── Theme ──────────────────────────────────────────────────────────────

    override suspend fun getTheme(): AppTheme {
        val raw = local.read(
            key          = LocalDataSourceEnum.THEME,
            defaultValue = AppTheme.SYSTEM.value,
            type         = String::class,
        )
        return AppTheme.fromValue(raw)
    }

    override suspend fun saveTheme(theme: AppTheme) {
        local.save(
            key   = LocalDataSourceEnum.THEME,
            value = theme.value,
            type  = String::class,
        )
    }

    // ── Language ───────────────────────────────────────────────────────────

    override suspend fun getLanguage(): String =
        local.read(
            key          = LocalDataSourceEnum.LANGUAGE,
            defaultValue = "ar",   // Arabic default per app identity
            type         = String::class,
        )

    override suspend fun saveLanguage(code: String) {
        local.save(
            key   = LocalDataSourceEnum.LANGUAGE,
            value = code,
            type  = String::class,
        )
    }

    // ── Notifications ──────────────────────────────────────────────────────

    override suspend fun getNotificationsEnabled(): Boolean =
        local.read(
            key          = LocalDataSourceEnum.NOTIFICATIONS_ENABLED,
            defaultValue = true,
            type         = Boolean::class,
        )

    override suspend fun saveNotificationsEnabled(enabled: Boolean) {
        local.save(
            key   = LocalDataSourceEnum.NOTIFICATIONS_ENABLED,
            value = enabled,
            type  = Boolean::class,
        )
    }
}

package com.daragly.daraglyhair.feature.settings.domain.repository

import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme

interface ISettingsRepository {
    // ── Theme ──────────────────────────────────────────────────────────────
    suspend fun getTheme(): AppTheme
    suspend fun saveTheme(theme: AppTheme)

    // ── Language ───────────────────────────────────────────────────────────
    suspend fun getLanguage(): String   // "en" | "ar"
    suspend fun saveLanguage(code: String)

    // ── Notifications (local preference only — no notification endpoint on server) ──
    suspend fun getNotificationsEnabled(): Boolean
    suspend fun saveNotificationsEnabled(enabled: Boolean)
}

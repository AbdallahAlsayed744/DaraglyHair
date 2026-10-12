package com.daragly.daraglyhair.feature.settings.ui

import androidx.compose.ui.graphics.painter.Painter
import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme
import com.daragly.daraglyhair.feature.user.domain.model.User

// ── UI State ──────────────────────────────────────────────────────────────────

data class SettingsState(
    val user: User = User(),
    val isLoadingUser: Boolean = true,
    // Resolved image painter from the stored URI — null means use the default avatar icon
    val userImagePainter: Painter? = null,
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: String = "ar",
    val notificationsEnabled: Boolean = true,
    val isSigningOut: Boolean = false,
    val showSignOutDialog: Boolean = false,
)

// ── User intents ──────────────────────────────────────────────────────────────

sealed interface SettingsAction {
    // Lifecycle
    data object LoadSettings : SettingsAction
    data object ReloadUser : SettingsAction

    // Appearance
    data class ThemeSelected(val theme: AppTheme) : SettingsAction

    // Language
    data class LanguageSelected(val code: String) : SettingsAction

    // Notifications
    data class NotificationsToggled(val enabled: Boolean) : SettingsAction

    // Image painter resolved from URI (composable side pushes this in)
    data class UserImagePainterResolved(val painter: androidx.compose.ui.graphics.painter.Painter?) : SettingsAction

    // Navigation
    data object NavigateBack : SettingsAction
    data object EditProfileClicked : SettingsAction
    data object ChangePasswordClicked : SettingsAction
    data object NotificationsClicked : SettingsAction
    data object PrivacyPolicyClicked : SettingsAction
    data object TermsClicked : SettingsAction

    // Sign-out
    data object SignOutClicked : SettingsAction
    data object SignOutConfirmed : SettingsAction
    data object SignOutDismissed : SettingsAction
}

// ── One-shot side effects ─────────────────────────────────────────────────────

sealed interface SettingsEvent {
    data object NavigateBack : SettingsEvent
    data object NavigateToLogin : SettingsEvent
    data object NavigateToProfile : SettingsEvent
    data object NavigateToEditProfile : SettingsEvent
    data object NavigateToForgotPassword : SettingsEvent
    data object NavigateToNotifications : SettingsEvent
    data class OpenUrl(val url: String) : SettingsEvent
}

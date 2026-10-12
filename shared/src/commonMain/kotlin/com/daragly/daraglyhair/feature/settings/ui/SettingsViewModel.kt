package com.daragly.daraglyhair.feature.settings.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.language.ILanguageEvent
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.theme.IThemeEvent
import com.daragly.daraglyhair.feature.authentication.domain.interactors.FetchAndSaveUserProfileUseCase
import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme
import com.daragly.daraglyhair.feature.settings.domain.usecase.GetLanguageUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.GetNotificationsEnabledUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.GetThemeUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.SaveLanguageUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.SaveNotificationsEnabledUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.SaveThemeUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.GetUserUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.LogoutUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class SettingsViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val fetchAndSaveUserProfileUseCase: FetchAndSaveUserProfileUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    private val getLanguageUseCase: GetLanguageUseCase,
    private val saveLanguageUseCase: SaveLanguageUseCase,
    private val getNotificationsEnabledUseCase: GetNotificationsEnabledUseCase,
    private val saveNotificationsEnabledUseCase: SaveNotificationsEnabledUseCase,
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<SettingsState, SettingsAction, SettingsEvent>(SettingsState()) {

    private val languageEventController: IEventController<ILanguageEvent>
            by inject(qualifier = named("LanguageEvent"))

    private val themeEventController: IEventController<IThemeEvent>
            by inject(qualifier = named("ThemeEvent"))

    init {
        onAction(SettingsAction.LoadSettings)
    }

    override fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.LoadSettings             -> loadSettings()
            is SettingsAction.ReloadUser               -> reloadUser()
            is SettingsAction.UserImagePainterResolved -> updateState { copy(userImagePainter = action.painter) }
            is SettingsAction.ThemeSelected            -> onThemeSelected(action.theme)
            is SettingsAction.LanguageSelected         -> onLanguageSelected(action.code)
            is SettingsAction.NotificationsToggled     -> onNotificationsToggled(action.enabled)
            is SettingsAction.EditProfileClicked       -> emitEvent(SettingsEvent.NavigateToEditProfile)
            is SettingsAction.NavigateBack             -> emitEvent(SettingsEvent.NavigateBack)
            is SettingsAction.ChangePasswordClicked    -> emitEvent(SettingsEvent.NavigateToForgotPassword)
            is SettingsAction.NotificationsClicked     -> emitEvent(SettingsEvent.NavigateToNotifications)
            is SettingsAction.PrivacyPolicyClicked     -> emitEvent(SettingsEvent.OpenUrl(PRIVACY_URL))
            is SettingsAction.TermsClicked             -> emitEvent(SettingsEvent.OpenUrl(TERMS_URL))
            is SettingsAction.SignOutClicked           -> updateState { copy(showSignOutDialog = true) }
            is SettingsAction.SignOutDismissed         -> updateState { copy(showSignOutDialog = false) }
            is SettingsAction.SignOutConfirmed         -> signOut()
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private fun loadSettings() {
        viewModelScope.launch {
            updateState { copy(isLoadingUser = true) }

            // 1. Try local first
            val localResult = getUserUseCase.invoke(Unit)
            val localUser   = (localResult as? Resource.Success)?.model

            if (localUser != null && localUser.name.isNotBlank()) {
                // We have persisted data — show it immediately
                updateState { copy(user = localUser, isLoadingUser = false) }
            } else {
                // No local data yet (first launch after login, or data was cleared)
                // Fetch from API, persist, then show
                runCatching { fetchAndSaveUserProfileUseCase.invoke(Unit) }
                when (val fresh = getUserUseCase.invoke(Unit)) {
                    is Resource.Success -> updateState { copy(user = fresh.model, isLoadingUser = false) }
                    else                -> updateState { copy(isLoadingUser = false) }
                }
            }

            // Load preferences (theme, language, notifications) in parallel
            when (val r = getThemeUseCase.invoke(Unit)) {
                is Resource.Success -> updateState { copy(theme = r.model) }
                else                -> Unit
            }
            when (val r = getLanguageUseCase.invoke(Unit)) {
                is Resource.Success -> updateState { copy(language = r.model) }
                else                -> Unit
            }
            when (val r = getNotificationsEnabledUseCase.invoke(Unit)) {
                is Resource.Success -> updateState { copy(notificationsEnabled = r.model) }
                else                -> Unit
            }
        }
    }

    /**
     * Reload user — called every time the Settings screen resumes
     * (e.g. after returning from EditProfileScreen with saved changes).
     * Shows local data immediately, then always refreshes from API in the
     * background so the header stays in sync with the latest server data.
     */
    private fun reloadUser() {
        viewModelScope.launch {
            // Show whatever is cached first
            val localResult = getUserUseCase.invoke(Unit)
            val localUser   = (localResult as? Resource.Success)?.model
            if (localUser != null && localUser.name.isNotBlank()) {
                updateState { copy(user = localUser, isLoadingUser = false) }
            } else {
                updateState { copy(isLoadingUser = true) }
            }

            // Always refresh from API so imageUrl and other fields stay current
            runCatching { fetchAndSaveUserProfileUseCase.invoke(Unit) }
            when (val fresh = getUserUseCase.invoke(Unit)) {
                is Resource.Success -> updateState { copy(user = fresh.model, isLoadingUser = false) }
                else                -> updateState { copy(isLoadingUser = false) }
            }
        }
    }

    // ── Theme ──────────────────────────────────────────────────────────────────

    private fun onThemeSelected(theme: AppTheme) {
        updateState { copy(theme = theme) }
        viewModelScope.launch {
            saveThemeUseCase.invoke(theme)
            // Emit global event so HallaakApp flips isDarkTheme immediately
            themeEventController.emit(IThemeEvent.ChangeTheme(theme))
        }
    }

    // ── Language ───────────────────────────────────────────────────────────────

    private fun onLanguageSelected(code: String) {
        if (state.value.language == code) return
        updateState { copy(language = code) }
        viewModelScope.launch {
            saveLanguageUseCase.invoke(code)
            // Dispatch the global language event — triggers AppCompatDelegate / LocaleManager
            languageEventController.emit(ILanguageEvent.ChangeLanguage(code))
        }
    }

    // ── Notifications ──────────────────────────────────────────────────────────

    private fun onNotificationsToggled(enabled: Boolean) {
        updateState { copy(notificationsEnabled = enabled) }
        viewModelScope.launch {
            saveNotificationsEnabledUseCase.invoke(enabled)
        }
    }

    // ── Sign out ───────────────────────────────────────────────────────────────

    private fun signOut() {
        updateState { copy(showSignOutDialog = false, isSigningOut = true) }
        viewModelScope.launch {
            when (logoutUseCase.invoke(Unit)) {
                is Resource.Success -> {
                    // Navigate to Login, clear the entire back-stack
                    fireNavigate(IAuthGraph.Login) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
                is Resource.Failure -> {
                    updateState { copy(isSigningOut = false) }
                    onGlobalError("فشل تسجيل الخروج — حاول مرة أخرى")
                }
                else -> updateState { copy(isSigningOut = false) }
            }
        }
    }

    companion object {
        private const val PRIVACY_URL = "https://barberhub-api.runasp.net/privacy"
        private const val TERMS_URL   = "https://barberhub-api.runasp.net/terms"
    }
}

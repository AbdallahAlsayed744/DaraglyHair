package com.daragly.daraglyhair.feature.authentication.ui.login

// ── State ─────────────────────────────────────────────────────────────────────
data class LoginState(
    val email: String         = "",
    val emailError: String?   = null,
    val password: String      = "",
    val passwordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean    = false,
)

// ── Actions (user intents) ────────────────────────────────────────────────────
sealed interface LoginAction {
    data class EmailChanged(val value: String)    : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data object TogglePasswordVisibility          : LoginAction
    data object LoginClicked                      : LoginAction
    data object RegisterClicked                   : LoginAction
    data object ForgotPasswordClicked             : LoginAction
}

// ── Events (one-shot side-effects) ────────────────────────────────────────────
sealed interface LoginEvent {
    data object NavigateToHome           : LoginEvent
    data object NavigateToRegister       : LoginEvent
    data object NavigateToForgotPassword : LoginEvent
    data class  ShowSnackbar(val msg: String) : LoginEvent
}

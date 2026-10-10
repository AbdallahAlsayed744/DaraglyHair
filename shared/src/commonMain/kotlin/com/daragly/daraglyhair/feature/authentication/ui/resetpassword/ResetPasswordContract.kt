package com.daragly.daraglyhair.feature.authentication.ui.resetpassword

data class ResetPasswordState(
    val password: String        = "",
    val passwordError: String?  = null,
    val token: String           = "",
    val tokenError: String?     = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean      = false,
)

sealed interface ResetPasswordAction {
    data class PasswordChanged(val value: String) : ResetPasswordAction
    data class TokenChanged(val value: String)    : ResetPasswordAction
    data object TogglePasswordVisibility          : ResetPasswordAction
    data object ConfirmClicked                    : ResetPasswordAction
    data object BackClicked                       : ResetPasswordAction
}

sealed interface ResetPasswordEvent {
    data object ResetSuccess                     : ResetPasswordEvent
    data object NavigateBack                     : ResetPasswordEvent
    data class  ShowSnackbar(val msg: String)    : ResetPasswordEvent
}

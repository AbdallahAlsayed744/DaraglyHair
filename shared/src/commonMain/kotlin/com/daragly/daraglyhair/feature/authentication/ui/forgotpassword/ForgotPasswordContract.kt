package com.daragly.daraglyhair.feature.authentication.ui.forgotpassword

data class ForgotPasswordState(
    val email: String       = "",
    val emailError: String? = null,
    val isLoading: Boolean  = false,
    val isSuccess: Boolean  = false,
)

sealed interface ForgotPasswordAction {
    data class EmailChanged(val value: String) : ForgotPasswordAction
    data object SendClicked                    : ForgotPasswordAction
    data object BackClicked                    : ForgotPasswordAction
}

sealed interface ForgotPasswordEvent {
    data object NavigateBack                     : ForgotPasswordEvent
    data class  ShowSnackbar(val msg: String)    : ForgotPasswordEvent
}

package com.daragly.daraglyhair.feature.authentication.ui.register

data class RegisterState(
    val firstName: String = "", val firstNameError: String? = null,
    val lastName: String = "", val lastNameError: String? = null,
    val userName: String = "", val userNameError: String? = null,
    val email: String = "", val emailError: String? = null,
    val phone: String = "", val phoneError: String? = null,
    val password: String = "", val passwordError: String? = null,
    val confirmPassword: String = "", val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface RegisterAction {
    data class FirstNameChanged(val v: String) : RegisterAction
    data class LastNameChanged(val v: String) : RegisterAction
    data class UserNameChanged(val v: String) : RegisterAction
    data class EmailChanged(val v: String) : RegisterAction
    data class PhoneChanged(val v: String) : RegisterAction
    data class PasswordChanged(val v: String) : RegisterAction
    data class ConfirmPasswordChanged(val v: String) : RegisterAction
    data object TogglePasswordVisibility : RegisterAction
    data object ToggleConfirmPasswordVisibility : RegisterAction
    data object RegisterClicked : RegisterAction
    data object LoginClicked : RegisterAction
}

sealed interface RegisterEvent {
    data object RegisterSuccess : RegisterEvent
    data object NavigateToLogin : RegisterEvent
    data class ShowSnackbar(val msg: String) : RegisterEvent
}

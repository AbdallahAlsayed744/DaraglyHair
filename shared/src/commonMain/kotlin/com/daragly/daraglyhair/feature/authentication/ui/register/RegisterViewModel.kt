package com.daragly.daraglyhair.feature.authentication.ui.register

import com.daragly.daraglyhair.common.data.repository.remote.ErrorKeyEnum
import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import com.daragly.daraglyhair.common.ui.message.HallaakMessageType
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.authentication.data.request.RegisterRequest
import com.daragly.daraglyhair.feature.authentication.domain.interactors.RegisterUseCase
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
) : BaseViewModel<RegisterState, RegisterAction, RegisterEvent>(RegisterState()) {

    override fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.FirstNameChanged        -> updateState { copy(firstName = action.v, firstNameError = null) }
            is RegisterAction.LastNameChanged         -> updateState { copy(lastName  = action.v, lastNameError  = null) }
            is RegisterAction.UserNameChanged         -> updateState { copy(userName  = action.v, userNameError  = null) }
            is RegisterAction.EmailChanged            -> updateState { copy(email     = action.v, emailError     = null) }
            is RegisterAction.PhoneChanged            -> updateState { copy(phone     = action.v, phoneError     = null) }
            is RegisterAction.PasswordChanged         -> updateState { copy(password  = action.v, passwordError  = null) }
            is RegisterAction.ConfirmPasswordChanged  -> updateState { copy(confirmPassword = action.v, confirmPasswordError = null) }
            is RegisterAction.TogglePasswordVisibility        -> updateState { copy(isPasswordVisible = !isPasswordVisible) }
            is RegisterAction.ToggleConfirmPasswordVisibility -> updateState { copy(isConfirmPasswordVisible = !isConfirmPasswordVisible) }
            is RegisterAction.LoginClicked    -> viewModelScope.launch { fireNavigateUp() }
            is RegisterAction.RegisterClicked -> register()
        }
    }

    private fun register() {
        viewModelScope.launch {
            val s = state.value
            registerUseCase.invoke(
                RegisterRequest(
                    firstName       = s.firstName.trim(),
                    lastName        = s.lastName.trim(),
                    email           = s.email.trim(),
                    userName        = s.userName.trim(),
                    password        = s.password,
                    confirmPassword = s.confirmPassword,
                    phoneNumber     = s.phone.trim(),
                )
            ).collectResource(
                onSuccess = {
                    fireMessage("Account created! Please sign in.", HallaakMessageType.SUCCESS)
                    fireNavigate(IAuthGraph.Login, builder = {
                        popUpTo(IAuthGraph.Register) { inclusive = true }
                    })
                },
            )
        }
    }

    override fun onRequestValidation(errors: Map<IErrorKeyEnum, String>) {
        updateState {
            copy(
                firstNameError       = errors[ErrorKeyEnum.NAME]?.let { "First name must be at least 2 characters" },
                emailError           = errors[ErrorKeyEnum.EMAIL]?.let { "Invalid email address" },
                phoneError           = errors[ErrorKeyEnum.PHONE_NUMBER]?.let { "Invalid phone number" },
                passwordError        = errors[ErrorKeyEnum.PASSWORD]?.let { "Password must be at least 8 characters" },
                confirmPasswordError = errors[ErrorKeyEnum.CONFIRMATION_PASSWORD]?.let { "Passwords do not match" },
            )
        }
    }
}

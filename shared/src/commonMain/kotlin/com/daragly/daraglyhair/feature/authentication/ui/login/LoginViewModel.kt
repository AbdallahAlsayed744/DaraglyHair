package com.daragly.daraglyhair.feature.authentication.ui.login

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.data.repository.remote.ErrorKeyEnum
import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import com.daragly.daraglyhair.common.ui.message.HallaakMessageType
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.authentication.data.request.LoginRequest
import com.daragly.daraglyhair.feature.authentication.domain.interactors.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : BaseViewModel<LoginState, LoginAction, LoginEvent>(LoginState()) {

    override fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged ->
                updateState { copy(email = action.value, emailError = null) }
            is LoginAction.PasswordChanged ->
                updateState { copy(password = action.value, passwordError = null) }
            is LoginAction.TogglePasswordVisibility ->
                updateState { copy(isPasswordVisible = !isPasswordVisible) }
            is LoginAction.LoginClicked          -> login()
            is LoginAction.RegisterClicked       -> fireNavigate(IAuthGraph.Register)
            is LoginAction.ForgotPasswordClicked -> fireNavigate(IAuthGraph.ForgotPassword)
        }
    }

    private fun login() {
        viewModelScope.launch {
            val s = state.value
            loginUseCase.invoke(
                LoginRequest(email = s.email.trim(), password = s.password)
            ).collectResource(
                onSuccess = {
                    fireMessage("Welcome back!", HallaakMessageType.SUCCESS)
                    fireNavigate(IMainGraph.Home, builder = {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    })
                },
            )
        }
    }

    override fun onRequestValidation(errors: Map<IErrorKeyEnum, String>) {
        updateState {
            copy(
                emailError    = errors[ErrorKeyEnum.EMAIL]?.let { "Invalid email address" },
                passwordError = errors[ErrorKeyEnum.PASSWORD]?.let { "Password must be at least 8 characters" },
            )
        }
    }
}

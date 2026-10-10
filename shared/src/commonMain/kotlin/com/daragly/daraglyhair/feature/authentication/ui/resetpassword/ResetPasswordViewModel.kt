package com.daragly.daraglyhair.feature.authentication.ui.resetpassword

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import com.daragly.daraglyhair.common.ui.message.HallaakMessageType
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.authentication.data.request.ResetPasswordRequest
import com.daragly.daraglyhair.feature.authentication.domain.interactors.ResetPasswordUseCase
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val email: String,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : BaseViewModel<ResetPasswordState, ResetPasswordAction, ResetPasswordEvent>(ResetPasswordState()) {

    override fun onAction(action: ResetPasswordAction) {
        when (action) {
            is ResetPasswordAction.PasswordChanged ->
                updateState { copy(password = action.value, passwordError = null) }
            is ResetPasswordAction.TokenChanged ->
                updateState { copy(token = action.value, tokenError = null) }
            is ResetPasswordAction.TogglePasswordVisibility ->
                updateState { copy(isPasswordVisible = !isPasswordVisible) }
            is ResetPasswordAction.BackClicked    ->
                viewModelScope.launch { fireNavigateUp() }
            is ResetPasswordAction.ConfirmClicked -> resetPassword()
        }
    }

    private fun resetPassword() {
        viewModelScope.launch {
            val s = state.value
            resetPasswordUseCase.invoke(
                ResetPasswordRequest(email = email, password = s.password, token = s.token.trim())
            ).collectResource(
                onSuccess = {
                    fireMessage("Password reset successfully!", HallaakMessageType.SUCCESS)
                    fireNavigate(IAuthGraph.Login, builder = {
                        popUpTo(0) { inclusive = true }
                    })
                },
            )
        }
    }

    override fun onRequestValidation(errors: Map<IErrorKeyEnum, String>) {
        updateState {
            copy(passwordError = errors.values.firstOrNull())
        }
    }
}

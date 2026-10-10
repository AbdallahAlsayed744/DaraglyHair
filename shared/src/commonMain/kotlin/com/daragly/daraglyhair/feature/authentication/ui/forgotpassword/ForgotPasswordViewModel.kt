package com.daragly.daraglyhair.feature.authentication.ui.forgotpassword

import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import com.daragly.daraglyhair.common.ui.message.HallaakMessageType
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.authentication.data.request.ForgotPasswordRequest
import com.daragly.daraglyhair.feature.authentication.domain.interactors.ForgotPasswordUseCase
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
) : BaseViewModel<ForgotPasswordState, ForgotPasswordAction, ForgotPasswordEvent>(ForgotPasswordState()) {

    override fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.EmailChanged ->
                updateState { copy(email = action.value, emailError = null) }
            is ForgotPasswordAction.BackClicked  ->
                viewModelScope.launch { fireNavigateUp() }
            is ForgotPasswordAction.SendClicked  -> sendReset()
        }
    }

    private fun sendReset() {
        viewModelScope.launch {
            forgotPasswordUseCase.invoke(ForgotPasswordRequest(email = state.value.email.trim()))
                .collectResource(
                    onSuccess = {
                        updateState { copy(isSuccess = true) }
                        fireMessage("Password reset email sent successfully", HallaakMessageType.SUCCESS)
                    },
                )
        }
    }

    override fun onRequestValidation(errors: Map<IErrorKeyEnum, String>) {
        updateState { copy(emailError = "Please enter a valid email address") }
    }
}

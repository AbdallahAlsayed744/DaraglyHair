package com.daragly.daraglyhair.feature.bookings.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.bookings.domain.interactors.CancelAppointmentUseCase
import com.daragly.daraglyhair.feature.bookings.domain.interactors.GetAppointmentDetailUseCase
import com.daragly.daraglyhair.feature.bookings.domain.model.CancellationRequest
import kotlinx.coroutines.launch

class AppointmentDetailViewModel(
    private val appointmentId: String,
    private val getAppointmentDetailUseCase: GetAppointmentDetailUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase,
) : BaseViewModel<AppointmentDetailState, AppointmentDetailAction, AppointmentDetailEvent>(
    AppointmentDetailState()
) {

    init {
        loadAppointment()
    }

    override fun onAction(action: AppointmentDetailAction) {
        when (action) {
            is AppointmentDetailAction.Load                    -> loadAppointment()
            is AppointmentDetailAction.Retry                   -> loadAppointment()
            is AppointmentDetailAction.BackClicked             -> emitEvent(AppointmentDetailEvent.NavigateBack)
            is AppointmentDetailAction.CancelClicked           -> updateState {
                copy(showCancellationDialog = true, cancellationReason = "")
            }
            is AppointmentDetailAction.CancellationReasonChanged -> updateState {
                copy(cancellationReason = action.reason)
            }
            is AppointmentDetailAction.ConfirmCancellation -> confirmCancellation(
                appointmentId = action.appointmentId,
                reason = action.reason,
            )
            is AppointmentDetailAction.DismissCancellationDialog -> updateState { copy(showCancellationDialog = false) }
            is AppointmentDetailAction.DismissCancellationResult -> updateState { copy(cancellationState = CancellationState.Idle) }
        }
    }

    private fun loadAppointment() {
        updateState { copy(content = AppointmentDetailContentState.Loading) }

        viewModelScope.launch {
            when (val result = getAppointmentDetailUseCase(appointmentId)) {
                is Resource.Success -> updateState {
                    copy(content = AppointmentDetailContentState.Success(result.model))
                }
                is Resource.Failure -> updateState {
                    copy(
                        content = AppointmentDetailContentState.Error(
                            result.exception.message ?: "فشل تحميل تفاصيل الحجز"
                        )
                    )
                }
                is Resource.Loading -> { /* set above */ }
            }
        }
    }

    private fun confirmCancellation(appointmentId: String, reason: String?) {
        updateState { copy(showCancellationDialog = false, cancellationState = CancellationState.InProgress) }

        viewModelScope.launch {
            when (val result = cancelAppointmentUseCase(CancellationRequest(appointmentId, reason))) {
                is Resource.Success -> {
                    // Reload appointment to reflect the updated status
                    loadAppointment()
                    updateState { copy(cancellationState = CancellationState.Success) }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(
                            cancellationState = CancellationState.Failure(
                                result.exception.message ?: "تعذّر إلغاء الحجز. حاول مرة أخرى."
                            )
                        )
                    }
                }
                is Resource.Loading -> { /* set above */ }
            }
        }
    }
}

package com.daragly.daraglyhair.feature.bookings.ui

import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment

// ── Screen state ──────────────────────────────────────────────────────────────

data class AppointmentDetailState(
    val content: AppointmentDetailContentState = AppointmentDetailContentState.Loading,
    val cancellationState: CancellationState = CancellationState.Idle,
    val showCancellationDialog: Boolean = false,
    val cancellationReason: String = "",
)

sealed class AppointmentDetailContentState {
    data object Loading : AppointmentDetailContentState()
    data class Success(val appointment: BookingAppointment) : AppointmentDetailContentState()
    data class Error(val message: String) : AppointmentDetailContentState()
}

sealed class CancellationState {
    data object Idle : CancellationState()
    data object InProgress : CancellationState()
    data object Success : CancellationState()
    data class Failure(val message: String) : CancellationState()
}

// ── User actions ──────────────────────────────────────────────────────────────

sealed interface AppointmentDetailAction {
    data object Load : AppointmentDetailAction
    data object Retry : AppointmentDetailAction
    data object BackClicked : AppointmentDetailAction
    data object CancelClicked : AppointmentDetailAction           // opens confirm dialog
    data class CancellationReasonChanged(val reason: String) : AppointmentDetailAction
    data class ConfirmCancellation(
        val appointmentId: String,
        val reason: String?,
    ) : AppointmentDetailAction                                   // user confirms in dialog
    data object DismissCancellationDialog : AppointmentDetailAction
    data object DismissCancellationResult : AppointmentDetailAction
}

// ── One-shot events ───────────────────────────────────────────────────────────

sealed interface AppointmentDetailEvent {
    data object NavigateBack : AppointmentDetailEvent
    data object NavigateBackAndRefresh : AppointmentDetailEvent   // after successful cancel
}

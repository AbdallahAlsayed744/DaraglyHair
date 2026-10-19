package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.CreateAppointmentUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraftStateHolder
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingSubmission
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookingReviewViewModel(
    private val shopId: String,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    private val draftHolder: BookingDraftStateHolder,
) : BaseViewModel<BookingReviewState, BookingReviewAction, BookingReviewEvent>(
    BookingReviewState(draft = draftHolder.current),
) {

    private var submitting = false

    init {
        viewModelScope.launch {
            draftHolder.draft.collectLatest { draft ->
                updateState { copy(draft = draft) }
            }
        }
    }

    override fun onAction(action: BookingReviewAction) {
        when (action) {
            is BookingReviewAction.NotesChanged -> draftHolder.setNotes(action.notes)
            is BookingReviewAction.ConfirmClicked -> confirmBooking()
            is BookingReviewAction.ChooseAnotherTimeClicked -> {
                updateState { copy(hasConflict = false) }
                emitEvent(BookingReviewEvent.NavigateToDateTime)
            }
            is BookingReviewAction.DismissMessages ->
                updateState { copy(hasConflict = false, errorMessage = null) }
        }
    }

    private fun confirmBooking() {
        // Prevent duplicate submissions while a booking request is in progress.
        if (submitting) return
        val draft = draftHolder.current
        if (draft.shopId != shopId || !draft.isValidForSubmission) return
        val slot = draft.slot ?: return
        val mode = draft.transportationMode ?: return
        val location = draft.userLocation ?: return

        submitting = true
        updateState { copy(isSubmitting = true, hasConflict = false, errorMessage = null) }

        viewModelScope.launch {
            val submission = BookingSubmission(
                shopId = shopId,
                shopBarberId = draft.shopBarberId,
                startAtUtc = slot.startAt,
                shopServiceIds = draft.selectedServices.map { it.shopServiceId },
                notes = draft.notes,
                userLatitude = location.latitude,
                userLongitude = location.longitude,
                transportationMode = mode,
            )
            when (val result = createAppointmentUseCase(submission)) {
                is Resource.Success -> {
                    submitting = false
                    val appointmentId = result.model.trim().trim('"')
                    draftHolder.clear()
                    updateState { copy(isSubmitting = false) }
                    emitEvent(BookingReviewEvent.NavigateToSuccess(shopId, appointmentId))
                }
                is Resource.Failure -> {
                    submitting = false
                    val exception = result.exception
                    val isConflict =
                        exception is DaraglyHairException.Client.Unhandled && exception.errorCode == 409
                    if (isConflict) {
                        // Slot vanished — invalidate it, preserve services/barber, let the user re-pick.
                        draftHolder.selectSlot(null)
                    }
                    updateState {
                        copy(
                            isSubmitting = false,
                            hasConflict = isConflict,
                            errorMessage = if (isConflict) null
                            else exception.message ?: GENERIC_FALLBACK,
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    companion object {
        private const val GENERIC_FALLBACK = "error"
    }
}

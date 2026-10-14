package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.bookings.domain.interactors.GetAppointmentDetailUseCase
import kotlinx.coroutines.launch

class BookingSuccessViewModel(
    private val shopId: String,
    private val appointmentId: String,
    private val getAppointmentDetailUseCase: GetAppointmentDetailUseCase,
) : BaseViewModel<BookingSuccessState, BookingSuccessAction, BookingSuccessEvent>(
    BookingSuccessState(appointmentId = appointmentId),
) {

    init {
        loadDetails()
    }

    override fun onAction(action: BookingSuccessAction) {
        when (action) {
            is BookingSuccessAction.RetryDetails -> loadDetails()
            is BookingSuccessAction.ViewDetailsClicked ->
                emitEvent(BookingSuccessEvent.NavigateToAppointmentDetail(appointmentId))
            is BookingSuccessAction.OpenBookingsClicked ->
                emitEvent(BookingSuccessEvent.OpenBookingsTab)
            is BookingSuccessAction.BackToShopClicked ->
                emitEvent(BookingSuccessEvent.NavigateToShopDetails(shopId))
        }
    }

    /** Authoritative details from the backend — failure never hides the success state itself. */
    private fun loadDetails() {
        updateState { copy(details = SectionState.Loading) }
        viewModelScope.launch {
            when (val result = getAppointmentDetailUseCase(appointmentId)) {
                is Resource.Success -> updateState { copy(details = SectionState.Data(result.model)) }
                is Resource.Failure -> updateState {
                    copy(details = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}

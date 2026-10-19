package com.daragly.daraglyhair.feature.bookings.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.bookings.domain.interactors.GetAppointmentsUseCase
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingTabFilter
import kotlinx.coroutines.launch

class BookingsViewModel(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
) : BaseViewModel<BookingsState, BookingsAction, BookingsEvent>(BookingsState()) {

    init {
        loadAppointments()
    }

    override fun onAction(action: BookingsAction) {
        when (action) {
            is BookingsAction.Load                -> loadAppointments()
            is BookingsAction.Refresh             -> loadAppointments()
            is BookingsAction.Retry               -> loadAppointments()
            is BookingsAction.TabSelected         -> onTabSelected(action.tab)
            is BookingsAction.AppointmentClicked  -> emitEvent(BookingsEvent.NavigateToDetail(action.appointmentId))
            is BookingsAction.WriteReviewClicked  -> emitEvent(BookingsEvent.NavigateToWriteReview(action.appointmentId, action.shopName))
            is BookingsAction.ExploreClicked      -> emitEvent(BookingsEvent.NavigateToExplore)
        }
    }

    private fun onTabSelected(tab: BookingTabFilter) {
        if (state.value.selectedTab == tab) return
        updateState { copy(selectedTab = tab, content = BookingsContentState.Loading) }
        loadAppointments(tab)
    }

    private fun loadAppointments(tab: BookingTabFilter = state.value.selectedTab) {
        updateState { copy(content = BookingsContentState.Loading) }

        viewModelScope.launch {
            when (val result = getAppointmentsUseCase(tab)) {
                is Resource.Success -> {
                    updateState {
                        copy(
                            content = if (result.model.isEmpty()) {
                                BookingsContentState.Empty
                            } else {
                                BookingsContentState.Success(result.model)
                            },
                        )
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(
                            content = BookingsContentState.Error(
                                result.exception.message ?: "فشل تحميل الحجوزات"
                            ),
                        )
                    }
                }
                is Resource.Loading -> { /* set above */ }
            }
        }
    }
}

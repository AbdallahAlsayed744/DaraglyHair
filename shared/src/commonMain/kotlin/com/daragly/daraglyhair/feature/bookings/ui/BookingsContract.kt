package com.daragly.daraglyhair.feature.bookings.ui

import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingTabFilter

// ── Screen state ──────────────────────────────────────────────────────────────

data class BookingsState(
    val selectedTab: BookingTabFilter = BookingTabFilter.UPCOMING,
    val content: BookingsContentState = BookingsContentState.Loading,
)

sealed class BookingsContentState {
    data object Loading : BookingsContentState()
    data class Success(val appointments: List<BookingAppointment>) : BookingsContentState()
    data object Empty : BookingsContentState()
    data class Error(val message: String) : BookingsContentState()
}

// ── User actions ──────────────────────────────────────────────────────────────

sealed interface BookingsAction {
    data object Load : BookingsAction
    data object Refresh : BookingsAction
    data object Retry : BookingsAction
    data class TabSelected(val tab: BookingTabFilter) : BookingsAction
    data class AppointmentClicked(val appointmentId: String) : BookingsAction
    data class WriteReviewClicked(val appointmentId: String, val shopName: String) : BookingsAction
    data object ExploreClicked : BookingsAction
}

// ── One-shot events ───────────────────────────────────────────────────────────

sealed interface BookingsEvent {
    data class NavigateToDetail(val appointmentId: String) : BookingsEvent
    data class NavigateToWriteReview(val appointmentId: String, val shopName: String) : BookingsEvent
    data object NavigateToExplore : BookingsEvent
}

package com.daragly.daraglyhair.feature.home.ui

import com.daragly.daraglyhair.feature.home.domain.model.Appointment
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.home.domain.model.ServiceCategory
import com.daragly.daraglyhair.feature.home.domain.model.UserProfile

// ── State ─────────────────────────────────────────────────────────────────────
data class HomeState(
    val profile: ProfileState = ProfileState.Loading,
    val categories: CategoriesState = CategoriesState.Loading,
    val nearbyShops: NearbyShopsState = NearbyShopsState.Loading,
    val upcomingAppointment: UpcomingAppointmentState = UpcomingAppointmentState.Loading,
    val searchKeyword: String = "",
    val isRefreshing: Boolean = false,
    val favoriteToggleInProgress: Set<String> = emptySet(),
)

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Success(val profile: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class CategoriesState {
    data object Loading : CategoriesState()
    data class Success(val categories: List<ServiceCategory>) : CategoriesState()
    data class Error(val message: String) : CategoriesState()
}

sealed class NearbyShopsState {
    data object Loading : NearbyShopsState()
    data class Success(val shops: List<Barbershop>) : NearbyShopsState()
    data class Empty(val message: String) : NearbyShopsState()
    data class Error(val message: String) : NearbyShopsState()
}

sealed class UpcomingAppointmentState {
    data object Loading : UpcomingAppointmentState()
    data class Success(val appointment: Appointment?) : UpcomingAppointmentState()
    data class Error(val message: String) : UpcomingAppointmentState()
}

// ── Actions (user intents) ────────────────────────────────────────────────────
sealed interface HomeAction {
    data object LoadHomeData : HomeAction
    data object Refresh : HomeAction
    data class SearchKeywordChanged(val keyword: String) : HomeAction
    data object ClearSearch : HomeAction
    data class ToggleFavorite(val shopId: String) : HomeAction
    data object RetryProfile : HomeAction
    data object RetryCategories : HomeAction
    data object RetryNearbyShops : HomeAction
    data object RetryUpcomingAppointment : HomeAction
    data object NotificationClicked : HomeAction
    data class CategoryClicked(val category: ServiceCategory) : HomeAction
    data object SeeAllShopsClicked : HomeAction
    data object ExploreClicked : HomeAction
    data class ShopCardClicked(val shopId: String) : HomeAction
    data object AppointmentCardClicked : HomeAction
}

// ── Events (one-shot side-effects) ────────────────────────────────────────────
sealed interface HomeEvent {
    data object NavigateToNotifications : HomeEvent
    data object NavigateToExplore : HomeEvent
    data class NavigateToExploreWithCategory(val categoryId: String) : HomeEvent
    data class NavigateToShopDetails(val shopId: String) : HomeEvent
    data object NavigateToAppointmentDetails : HomeEvent
}

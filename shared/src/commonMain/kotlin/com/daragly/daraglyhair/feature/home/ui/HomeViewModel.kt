package com.daragly.daraglyhair.feature.home.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.home.domain.model.isUpcoming
import com.daragly.daraglyhair.feature.home.domain.repository.IHomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: IHomeRepository,
) : BaseViewModel<HomeState, HomeAction, HomeEvent>(HomeState()) {

    private var searchJob: Job? = null

    init {
        loadHomeData()
    }

    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadHomeData -> loadHomeData()
            is HomeAction.Refresh -> refresh()
            is HomeAction.SearchKeywordChanged -> onSearchKeywordChanged(action.keyword)
            is HomeAction.ClearSearch -> clearSearch()
            is HomeAction.ToggleFavorite -> toggleFavorite(action.shopId)
            is HomeAction.RetryProfile -> loadProfile()
            is HomeAction.RetryCategories -> loadCategories()
            is HomeAction.RetryNearbyShops -> loadNearbyShops()
            is HomeAction.RetryUpcomingAppointment -> loadUpcomingAppointment()
            is HomeAction.NotificationClicked -> emitEvent(HomeEvent.NavigateToNotifications)
            is HomeAction.CategoryClicked -> emitEvent(HomeEvent.NavigateToExploreWithCategory(action.category.id))
            is HomeAction.SeeAllShopsClicked -> emitEvent(HomeEvent.NavigateToExplore)
            is HomeAction.ExploreClicked -> emitEvent(HomeEvent.NavigateToExplore)
            is HomeAction.ShopCardClicked -> emitEvent(HomeEvent.NavigateToShopDetails(action.shopId))
            is HomeAction.AppointmentCardClicked -> emitEvent(HomeEvent.NavigateToAppointmentDetails)
        }
    }

    private fun loadHomeData() {
        loadProfile()
        loadCategories()
        loadNearbyShops()
        loadUpcomingAppointment()
    }

    private fun refresh() {
        updateState { copy(isRefreshing = true) }
        loadHomeData()
        viewModelScope.launch {
            delay(500) // Minimum refresh duration for better UX
            updateState { copy(isRefreshing = false) }
        }
    }

    private fun onSearchKeywordChanged(keyword: String) {
        updateState { copy(searchKeyword = keyword) }
        
        // Debounce search
        searchJob?.cancel()
        if (keyword.isNotBlank()) {
            searchJob = viewModelScope.launch {
                delay(500) // Debounce delay
                searchBarbershops(keyword)
            }
        } else {
            loadNearbyShops()
        }
    }

    private fun clearSearch() {
        updateState { copy(searchKeyword = "") }
        loadNearbyShops()
    }

    private fun toggleFavorite(shopId: String) {
        val isAlreadyInProgress = state.value.favoriteToggleInProgress.contains(shopId)
        if (isAlreadyInProgress) return

        updateState {
            copy(favoriteToggleInProgress = favoriteToggleInProgress + shopId)
        }

        viewModelScope.launch {
            when (val result = repository.toggleFavorite(shopId)) {
                is Resource.Success -> {
                    val isNowFavorite = result.model
                    
                    // Update shop favorite status in the list
                    val currentState = state.value
                    when (val shopsState = currentState.nearbyShops) {
                        is NearbyShopsState.Success -> {
                            val updatedShops = shopsState.shops.map { shop ->
                                if (shop.id == shopId) {
                                    shop.copy(isFavorite = isNowFavorite)
                                } else {
                                    shop
                                }
                            }
                            updateState {
                                copy(
                                    nearbyShops = NearbyShopsState.Success(updatedShops),
                                    favoriteToggleInProgress = favoriteToggleInProgress - shopId
                                )
                            }
                        }
                        else -> {
                            updateState {
                                copy(favoriteToggleInProgress = favoriteToggleInProgress - shopId)
                            }
                        }
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(favoriteToggleInProgress = favoriteToggleInProgress - shopId)
                    }
                    onGlobalError(result.exception.message ?: "Failed to update favorite")
                }
                is Resource.Loading -> {} // Not applicable for this operation
            }
        }
    }

    private fun loadProfile() {
        updateState { copy(profile = ProfileState.Loading) }
        
        viewModelScope.launch {
            when (val result = repository.getUserProfile()) {
                is Resource.Success -> {
                    updateState { copy(profile = ProfileState.Success(result.model)) }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(profile = ProfileState.Error(result.exception.message ?: "Failed to load profile"))
                    }
                }
                is Resource.Loading -> {} // Already set to loading
            }
        }
    }

    private fun loadCategories() {
        updateState { copy(categories = CategoriesState.Loading) }
        
        viewModelScope.launch {
            when (val result = repository.getServiceCategories()) {
                is Resource.Success -> {
                    updateState { copy(categories = CategoriesState.Success(result.model)) }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(categories = CategoriesState.Error(result.exception.message ?: "Failed to load categories"))
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadNearbyShops() {
        updateState { copy(nearbyShops = NearbyShopsState.Loading) }
        
        viewModelScope.launch {
            when (val result = repository.searchBarbershops(pageNumber = 1, pageSize = 10)) {
                is Resource.Success -> {
                    if (result.model.isEmpty()) {
                        updateState {
                            copy(nearbyShops = NearbyShopsState.Empty("No barbershops found nearby"))
                        }
                    } else {
                        updateState {
                            copy(nearbyShops = NearbyShopsState.Success(result.model))
                        }
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(nearbyShops = NearbyShopsState.Error(result.exception.message ?: "Failed to load barbershops"))
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun searchBarbershops(keyword: String) {
        updateState { copy(nearbyShops = NearbyShopsState.Loading) }
        
        viewModelScope.launch {
            when (val result = repository.searchBarbershops(
                pageNumber = 1,
                pageSize = 20,
                keyword = keyword
            )) {
                is Resource.Success -> {
                    if (result.model.isEmpty()) {
                        updateState {
                            copy(nearbyShops = NearbyShopsState.Empty("No results for \"$keyword\""))
                        }
                    } else {
                        updateState {
                            copy(nearbyShops = NearbyShopsState.Success(result.model))
                        }
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(nearbyShops = NearbyShopsState.Error(result.exception.message ?: "Search failed"))
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun loadUpcomingAppointment() {
        updateState { copy(upcomingAppointment = UpcomingAppointmentState.Loading) }
        
        viewModelScope.launch {
            when (val result = repository.getUpcomingAppointment()) {
                is Resource.Success -> {
                    updateState {
                        copy(upcomingAppointment = UpcomingAppointmentState.Success(result.model))
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(upcomingAppointment = UpcomingAppointmentState.Error(result.exception.message ?: "Failed to load appointment"))
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }
}

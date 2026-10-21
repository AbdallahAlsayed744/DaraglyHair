package com.daragly.daraglyhair.feature.explore.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.location.UserLocationHolder
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.home.domain.repository.IHomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DEBOUNCE_DELAY_MS = 500L
private const val PAGE_SIZE = 20

class ExploreViewModel(
    private val repository: IHomeRepository,
    private val userLocationHolder: UserLocationHolder,
) : BaseViewModel<ExploreState, ExploreAction, ExploreEvent>(ExploreState()) {

    private var searchJob: Job? = null

    // Bumped on every search; only the newest generation may write state, so a slow
    // earlier response can never overwrite a newer one.
    private var searchGeneration = 0
    private var lastSearchAttempted = false
    private var lastSearchHadLocation = true

    override fun onAction(action: ExploreAction) {
        when (action) {
            is ExploreAction.Init                  -> init(action.initialQuery)
            is ExploreAction.LocationReady         -> onLocationReady()
            is ExploreAction.SearchKeywordChanged  -> onKeywordChanged(action.keyword)
            is ExploreAction.DistanceChanged       -> onDistanceChanged(action.maxDistanceKm)
            is ExploreAction.ClearSearch           -> clearSearch()
            is ExploreAction.Retry                 -> retry()
            is ExploreAction.ToggleFavorite        -> toggleFavorite(action.shopId)
            is ExploreAction.ShopCardClicked       -> emitEvent(ExploreEvent.NavigateToShopDetails(action.shopId))
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private fun init(initialQuery: String) {
        val current = state.value
        // A search for this keyword is already running or already shown (screen re-entry) — don't re-fire.
        val sameQuery = initialQuery == current.searchKeyword
        val alreadyCovered = current.shopsState is ExploreShopsState.Loading ||
            current.shopsState is ExploreShopsState.Success
        if (sameQuery && alreadyCovered) return

        // Cancel any pending debounced search so it can't overwrite the entry query.
        searchJob?.cancel()
        searchJob = null

        if (initialQuery.isNotBlank()) {
            updateState { copy(searchKeyword = initialQuery) }
            searchShops(initialQuery)
        } else {
            // Load all shops on open with no query
            searchShops("")
        }
    }

    private fun onLocationReady() {
        // The permission callback also fires on plain entry; only re-run when the
        // previous attempt actually went out without coordinates.
        if (!lastSearchAttempted) return
        if (lastSearchHadLocation) return
        searchShops(state.value.searchKeyword)
    }

    private fun onKeywordChanged(keyword: String) {
        updateState { copy(searchKeyword = keyword) }

        // Cancel any pending search
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY_MS)
            searchShops(keyword)
        }
    }

    private fun onDistanceChanged(maxDistanceKm: Int) {
        val clamped = maxDistanceKm.coerceIn(MIN_DISTANCE_KM, MAX_DISTANCE_KM)
        if (clamped == state.value.maxDistanceKm) return
        updateState { copy(maxDistanceKm = clamped) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY_MS)
            searchShops(state.value.searchKeyword)
        }
    }

    private fun clearSearch() {
        searchJob?.cancel()
        updateState { copy(searchKeyword = "") }
        searchShops("")
    }

    private fun retry() {
        searchShops(state.value.searchKeyword)
    }

    // ── Core search ───────────────────────────────────────────────────────────

    private fun searchShops(keyword: String) {
        val generation = ++searchGeneration
        lastSearchAttempted = true
        updateState { copy(shopsState = ExploreShopsState.Loading) }

        viewModelScope.launch {
            val location = userLocationHolder.currentLocation()
            lastSearchHadLocation = location != null
            val result = repository.searchBarbershops(
                pageNumber = 1,
                pageSize = PAGE_SIZE,
                keyword = keyword.takeIf { it.isNotBlank() },
                latitude = location?.latitude,
                longitude = location?.longitude,
                maxDistanceKm = state.value.maxDistanceKm.toDouble(),
            )
            // A newer search superseded this one — drop the stale response.
            if (generation != searchGeneration) return@launch
            when (result) {
                is Resource.Success -> {
                    updateState {
                        copy(
                            shopsState = if (result.model.isEmpty()) {
                                ExploreShopsState.Empty(keyword)
                            } else {
                                ExploreShopsState.Success(result.model)
                            },
                        )
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(
                            shopsState = ExploreShopsState.Error(
                                result.exception.message ?: "حدث خطأ أثناء البحث"
                            ),
                        )
                    }
                }
                is Resource.Loading -> { /* handled via state set above */ }
            }
        }
    }

    // ── Favorite toggle ───────────────────────────────────────────────────────

    private fun toggleFavorite(shopId: String) {
        if (state.value.favoriteToggleInProgress.contains(shopId)) return

        updateState { copy(favoriteToggleInProgress = favoriteToggleInProgress + shopId) }

        viewModelScope.launch {
            when (val result = repository.toggleFavorite(shopId)) {
                is Resource.Success -> {
                    val isNowFavorite = result.model
                    val current = state.value.shopsState
                    if (current is ExploreShopsState.Success) {
                        val updated = current.shops.map { shop ->
                            if (shop.id == shopId) shop.copy(isFavorite = isNowFavorite) else shop
                        }
                        updateState {
                            copy(
                                shopsState = ExploreShopsState.Success(updated),
                                favoriteToggleInProgress = favoriteToggleInProgress - shopId,
                            )
                        }
                    } else {
                        updateState {
                            copy(favoriteToggleInProgress = favoriteToggleInProgress - shopId)
                        }
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(favoriteToggleInProgress = favoriteToggleInProgress - shopId)
                    }
                    onGlobalError(result.exception.message ?: "فشل تحديث المفضلة")
                }
                is Resource.Loading -> {}
            }
        }
    }
}

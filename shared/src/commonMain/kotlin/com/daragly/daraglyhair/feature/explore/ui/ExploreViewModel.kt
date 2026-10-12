package com.daragly.daraglyhair.feature.explore.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.home.domain.repository.IHomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DEBOUNCE_DELAY_MS = 500L
private const val PAGE_SIZE = 20

class ExploreViewModel(
    private val repository: IHomeRepository,
) : BaseViewModel<ExploreState, ExploreAction, ExploreEvent>(ExploreState()) {

    private var searchJob: Job? = null

    override fun onAction(action: ExploreAction) {
        when (action) {
            is ExploreAction.Init                  -> init(action.initialQuery)
            is ExploreAction.SearchKeywordChanged  -> onKeywordChanged(action.keyword)
            is ExploreAction.ClearSearch           -> clearSearch()
            is ExploreAction.Retry                 -> retry()
            is ExploreAction.ToggleFavorite        -> toggleFavorite(action.shopId)
            is ExploreAction.ShopCardClicked       -> emitEvent(ExploreEvent.NavigateToShopDetails(action.shopId))
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private fun init(initialQuery: String) {
        if (initialQuery.isNotBlank()) {
            updateState { copy(searchKeyword = initialQuery) }
            searchShops(initialQuery)
        } else {
            // Load all shops on open with no query
            searchShops("")
        }
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
        updateState { copy(shopsState = ExploreShopsState.Loading) }

        viewModelScope.launch {
            val result = repository.searchBarbershops(
                pageNumber = 1,
                pageSize = PAGE_SIZE,
                keyword = keyword.takeIf { it.isNotBlank() },
            )
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

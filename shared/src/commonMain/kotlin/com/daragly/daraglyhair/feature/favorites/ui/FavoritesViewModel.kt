package com.daragly.daraglyhair.feature.favorites.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.favorites.domain.interactors.GetFavoriteShopsUseCase
import com.daragly.daraglyhair.feature.favorites.domain.interactors.ToggleFavoriteUseCase
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteShopsUseCase: GetFavoriteShopsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : BaseViewModel<FavoritesState, FavoritesAction, FavoritesEvent>(FavoritesState()) {

    init {
        loadFavorites()
    }

    override fun onAction(action: FavoritesAction) {
        when (action) {
            is FavoritesAction.LoadFavorites   -> loadFavorites()
            is FavoritesAction.Refresh         -> loadFavorites()
            is FavoritesAction.Retry           -> loadFavorites()
            is FavoritesAction.ToggleFavorite  -> toggleFavorite(action.shopId)
            is FavoritesAction.ShopCardClicked -> emitEvent(FavoritesEvent.NavigateToShopDetails(action.shopId))
            is FavoritesAction.ExploreClicked  -> emitEvent(FavoritesEvent.NavigateToExplore)
        }
    }

    // ── Load ──────────────────────────────────────────────────────────────────

    private fun loadFavorites() {
        updateState { copy(content = FavoritesContentState.Loading) }

        viewModelScope.launch {
            when (val result = getFavoriteShopsUseCase(Unit)) {
                is Resource.Success -> {
                    updateState {
                        copy(
                            content = if (result.model.isEmpty()) {
                                FavoritesContentState.Empty
                            } else {
                                FavoritesContentState.Success(result.model)
                            },
                        )
                    }
                }

                is Resource.Failure -> {
                    // On a refresh, preserve an already-loaded list rather than
                    // replacing it with an error state.
                    val existing = state.value.content
                    if (existing !is FavoritesContentState.Success) {
                        updateState {
                            copy(
                                content = FavoritesContentState.Error(
                                    result.exception.message ?: "Failed to load favorites"
                                ),
                            )
                        }
                    }
                    onGlobalError(result.exception.message ?: "Failed to load favorites")
                }

                is Resource.Loading -> { /* already set to Loading above */ }
            }
        }
    }

    // ── Toggle ────────────────────────────────────────────────────────────────

    private fun toggleFavorite(shopId: String) {
        // Guard: prevent duplicate concurrent requests for the same shop
        if (state.value.favoriteToggleInProgress.contains(shopId)) return

        // Guard: list must be loaded before we can optimistically update it
        val currentContent = state.value.content
        val originalShops = (currentContent as? FavoritesContentState.Success)?.shops ?: return

        // Mark request as in-progress
        updateState { copy(favoriteToggleInProgress = favoriteToggleInProgress + shopId) }

        viewModelScope.launch {
            when (val result = toggleFavoriteUseCase(shopId)) {
                is Resource.Success -> {
                    val isNowFavorite = result.model

                    updateState {
                        val updatedShops = if (!isNowFavorite) {
                            // Server confirmed removal — drop from the list
                            originalShops.filter { it.id != shopId }
                        } else {
                            // Server confirmed addition (re-add edge case)
                            originalShops.map { shop ->
                                if (shop.id == shopId) shop.copy(isFavorite = true) else shop
                            }
                        }

                        copy(
                            content = if (updatedShops.isEmpty()) {
                                FavoritesContentState.Empty
                            } else {
                                FavoritesContentState.Success(updatedShops)
                            },
                            favoriteToggleInProgress = favoriteToggleInProgress - shopId,
                        )
                    }
                }

                is Resource.Failure -> {
                    // Roll back: restore the original list and clear in-progress flag
                    updateState {
                        copy(
                            content = FavoritesContentState.Success(originalShops),
                            favoriteToggleInProgress = favoriteToggleInProgress - shopId,
                        )
                    }
                    onGlobalError(result.exception.message ?: "Failed to update favorite")
                }

                is Resource.Loading -> { /* not applicable for a single toggle call */ }
            }
        }
    }
}

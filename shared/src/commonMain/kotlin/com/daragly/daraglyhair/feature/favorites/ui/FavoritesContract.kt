package com.daragly.daraglyhair.feature.favorites.ui

import com.daragly.daraglyhair.feature.home.domain.model.Barbershop

// ── Screen state ──────────────────────────────────────────────────────────────

data class FavoritesState(
    /** The content state of the list — mutually exclusive loading / success / error. */
    val content: FavoritesContentState = FavoritesContentState.Loading,
    /** Shop IDs for which a toggle request is currently in flight. */
    val favoriteToggleInProgress: Set<String> = emptySet(),
)

sealed class FavoritesContentState {
    /** Initial load (or explicit refresh) in progress. */
    data object Loading : FavoritesContentState()

    /** At least one favorite was returned by the API. */
    data class Success(val shops: List<Barbershop>) : FavoritesContentState()

    /** The API confirmed zero favorites. */
    data object Empty : FavoritesContentState()

    /** The API request failed. */
    data class Error(val message: String) : FavoritesContentState()
}

// ── User actions ──────────────────────────────────────────────────────────────

sealed interface FavoritesAction {
    /** Triggered by the screen on first composition. */
    data object LoadFavorites : FavoritesAction

    /** Pull-to-refresh. */
    data object Refresh : FavoritesAction

    /** User taps the heart icon on a card. */
    data class ToggleFavorite(val shopId: String) : FavoritesAction

    /** User taps a shop card body. */
    data class ShopCardClicked(val shopId: String) : FavoritesAction

    /** User taps "Explore Barbershops" in the empty state. */
    data object ExploreClicked : FavoritesAction

    /** User taps Retry in the error state. */
    data object Retry : FavoritesAction
}

// ── One-shot events ───────────────────────────────────────────────────────────

sealed interface FavoritesEvent {
    data class NavigateToShopDetails(val shopId: String) : FavoritesEvent
    data object NavigateToExplore : FavoritesEvent
}

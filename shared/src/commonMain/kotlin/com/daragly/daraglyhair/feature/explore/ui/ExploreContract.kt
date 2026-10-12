package com.daragly.daraglyhair.feature.explore.ui

import com.daragly.daraglyhair.feature.home.domain.model.Barbershop

// ── State ─────────────────────────────────────────────────────────────────────

data class ExploreState(
    val searchKeyword: String = "",
    val shopsState: ExploreShopsState = ExploreShopsState.Idle,
    val favoriteToggleInProgress: Set<String> = emptySet(),
)

sealed class ExploreShopsState {
    /** Screen just opened, no search triggered yet */
    data object Idle : ExploreShopsState()
    data object Loading : ExploreShopsState()
    data class Success(val shops: List<Barbershop>) : ExploreShopsState()
    data class Empty(val query: String) : ExploreShopsState()
    data class Error(val message: String) : ExploreShopsState()
}

// ── Actions ───────────────────────────────────────────────────────────────────

sealed interface ExploreAction {
    /** Called on first composition (optionally with a pre-filled query from Home) */
    data class Init(val initialQuery: String = "") : ExploreAction
    data class SearchKeywordChanged(val keyword: String) : ExploreAction
    data object ClearSearch : ExploreAction
    data object Retry : ExploreAction
    data class ToggleFavorite(val shopId: String) : ExploreAction
    data class ShopCardClicked(val shopId: String) : ExploreAction
}

// ── Events ────────────────────────────────────────────────────────────────────

sealed interface ExploreEvent {
    data class NavigateToShopDetails(val shopId: String) : ExploreEvent
}

package com.daragly.daraglyhair.common.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.daragly.daraglyhair.feature.home.ui.HomeScreen
import kotlinx.serialization.Serializable

// ── Destinations ──────────────────────────────────────────────────────────────

sealed interface IMainGraph : IDestination {
    @Serializable data object MainGraph : IGraph      // graph root
    @Serializable data object Home      : IMainGraph
}

// ── Nav graph builder ─────────────────────────────────────────────────────────

fun NavGraphBuilder.buildMainNavGraph() {
    navigation<IMainGraph.MainGraph>(startDestination = IMainGraph.Home) {
        composable<IMainGraph.Home> { HomeScreen() }
    }
}

package com.daragly.daraglyhair.common.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.daragly.daraglyhair.feature.bookings.ui.BookingsScreen
import com.daragly.daraglyhair.feature.explore.ui.ExploreScreen
import com.daragly.daraglyhair.feature.favorites.ui.FavoritesScreen
import com.daragly.daraglyhair.feature.home.ui.HomeScreen
import com.daragly.daraglyhair.feature.profile.ui.ProfileScreen
import kotlinx.serialization.Serializable

// ── Main Graph Destinations ───────────────────────────────────────────────────
//
//  Defines 5 bottom navigation destinations:
//    • Home      — الرئيسية   (main dashboard)
//    • Explore   — اكتشف      (search/map/filters)
//    • Bookings  — حجوزاتي    (upcoming/past appointments)
//    • Favorites — المفضلة    (saved barbershops)
//    • Profile   — حسابي      (account settings)
// ──────────────────────────────────────────────────────────────────────────────

sealed interface IMainGraph : IDestination {
    @Serializable data object MainGraph  : IGraph       // graph root
    @Serializable data object Home       : IMainGraph
    @Serializable data object Explore    : IMainGraph
    @Serializable data object Bookings   : IMainGraph
    @Serializable data object Favorites  : IMainGraph
    @Serializable data object Profile    : IMainGraph
}

// ── Nav graph builder ─────────────────────────────────────────────────────────

fun NavGraphBuilder.buildMainNavGraph() {
    navigation<IMainGraph.MainGraph>(startDestination = IMainGraph.Home) {
        composable<IMainGraph.Home>      { HomeScreen() }
        composable<IMainGraph.Explore>   { ExploreScreen() }
        composable<IMainGraph.Bookings>  { BookingsScreen() }
        composable<IMainGraph.Favorites> { FavoritesScreen() }
        composable<IMainGraph.Profile>   { ProfileScreen() }
    }
}

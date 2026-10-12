package com.daragly.daraglyhair.common.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.daragly.daraglyhair.feature.bookings.ui.BookingsScreen
import com.daragly.daraglyhair.feature.explore.ui.ExploreScreen
import com.daragly.daraglyhair.feature.favorites.ui.FavoritesScreen
import com.daragly.daraglyhair.feature.home.ui.HomeScreen
import com.daragly.daraglyhair.feature.profile.ui.EditProfileScreen
import com.daragly.daraglyhair.feature.settings.ui.SettingsScreen
import kotlinx.serialization.Serializable

// ── Main Graph Destinations ───────────────────────────────────────────────────
//
//  Five bottom-navigation tabs + two nested screens:
//    • Profile tab   → opens SettingsScreen directly
//    • EditProfile   → nested, opened from Settings → Account → Edit Profile
// ──────────────────────────────────────────────────────────────────────────────

sealed interface IMainGraph : IDestination {
    @Serializable data object MainGraph   : IGraph
    @Serializable data object Home        : IMainGraph
    /** Navigate to Explore, optionally pre-filling the search bar. */
    @Serializable data class  Explore(val query: String = "") : IMainGraph
    @Serializable data object Bookings    : IMainGraph
    @Serializable data object Favorites   : IMainGraph
    @Serializable data object Profile     : IMainGraph   // tab — renders SettingsScreen
    @Serializable data object Settings    : IMainGraph   // alias kept for back-compat
    @Serializable data object EditProfile : IMainGraph   // nested — Edit Profile form
}

// ── Nav graph builder ─────────────────────────────────────────────────────────

fun NavGraphBuilder.buildMainNavGraph() {
    navigation<IMainGraph.MainGraph>(startDestination = IMainGraph.Home) {
        composable<IMainGraph.Home>        { HomeScreen() }
        composable<IMainGraph.Explore>     { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.Explore>()
            ExploreScreen(initialQuery = route.query)
        }
        composable<IMainGraph.Bookings>    { BookingsScreen() }
        composable<IMainGraph.Favorites>   { FavoritesScreen() }
        // Profile tab and Settings route both show the same SettingsScreen
        composable<IMainGraph.Profile>     { SettingsScreen() }
        composable<IMainGraph.Settings>    { SettingsScreen() }
        composable<IMainGraph.EditProfile> { EditProfileScreen() }
    }
}


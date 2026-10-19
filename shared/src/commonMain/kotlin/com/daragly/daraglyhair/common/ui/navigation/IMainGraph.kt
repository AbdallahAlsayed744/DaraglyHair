package com.daragly.daraglyhair.common.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.daragly.daraglyhair.feature.bookings.ui.AppointmentDetailScreen
import com.daragly.daraglyhair.feature.bookings.ui.BookingsScreen
import com.daragly.daraglyhair.feature.explore.ui.ExploreScreen
import com.daragly.daraglyhair.feature.favorites.ui.FavoritesScreen
import com.daragly.daraglyhair.feature.home.ui.HomeScreen
import com.daragly.daraglyhair.feature.profile.ui.EditProfileScreen
import com.daragly.daraglyhair.feature.reviews.ui.ShopReviewsScreen
import com.daragly.daraglyhair.feature.reviews.ui.WriteReviewScreen
import com.daragly.daraglyhair.feature.settings.ui.SettingsScreen
import com.daragly.daraglyhair.feature.shopbooking.ui.BarberDetailsScreen
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingDateTimeScreen
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingReviewScreen
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingServicesScreen
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingSuccessScreen
import com.daragly.daraglyhair.feature.shopbooking.ui.ShopDetailsScreen
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
    /** Appointment detail screen — nested inside the Bookings flow. */
    @Serializable data class  AppointmentDetail(val appointmentId: String) : IMainGraph
    @Serializable data object Favorites   : IMainGraph
    @Serializable data object Profile     : IMainGraph   // tab — renders SettingsScreen
    @Serializable data object Settings    : IMainGraph   // alias kept for back-compat
    @Serializable data object EditProfile : IMainGraph   // nested — Edit Profile form

    // ── Shop details & booking flow (nested, hide the bottom bar) ───────────
    @Serializable data class  ShopDetails(val shopId: String) : IMainGraph
    @Serializable data class  BarberDetails(val shopId: String, val barberId: String) : IMainGraph
    @Serializable data class  BookingServices(val shopId: String) : IMainGraph
    @Serializable data class  BookingDateTime(val shopId: String) : IMainGraph
    @Serializable data class  BookingReview(val shopId: String) : IMainGraph
    @Serializable data class  BookingSuccess(val shopId: String, val appointmentId: String) : IMainGraph

    // ── Reviews (nested) ─────────────────────────────────────────────────────
    @Serializable data class  ShopReviews(val shopId: String) : IMainGraph
    @Serializable data class  WriteReview(val appointmentId: String, val shopName: String = "") : IMainGraph
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
        composable<IMainGraph.AppointmentDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.AppointmentDetail>()
            AppointmentDetailScreen(appointmentId = route.appointmentId)
        }
        composable<IMainGraph.Favorites>   { FavoritesScreen() }
        // Profile tab and Settings route both show the same SettingsScreen
        composable<IMainGraph.Profile>     { SettingsScreen() }
        composable<IMainGraph.Settings>    { SettingsScreen() }
        composable<IMainGraph.EditProfile> { EditProfileScreen() }

        // ── Shop details & booking flow ──────────────────────────────────────
        composable<IMainGraph.ShopDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.ShopDetails>()
            ShopDetailsScreen(shopId = route.shopId)
        }
        composable<IMainGraph.BarberDetails> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.BarberDetails>()
            BarberDetailsScreen(shopId = route.shopId, barberId = route.barberId)
        }
        composable<IMainGraph.BookingServices> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.BookingServices>()
            BookingServicesScreen(shopId = route.shopId)
        }
        composable<IMainGraph.BookingDateTime> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.BookingDateTime>()
            BookingDateTimeScreen(shopId = route.shopId)
        }
        composable<IMainGraph.BookingReview> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.BookingReview>()
            BookingReviewScreen(shopId = route.shopId)
        }
        composable<IMainGraph.BookingSuccess> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.BookingSuccess>()
            BookingSuccessScreen(shopId = route.shopId, appointmentId = route.appointmentId)
        }

        // ── Reviews ──────────────────────────────────────────────────────────
        composable<IMainGraph.ShopReviews> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.ShopReviews>()
            ShopReviewsScreen(shopId = route.shopId)
        }
        composable<IMainGraph.WriteReview> { backStackEntry ->
            val route = backStackEntry.toRoute<IMainGraph.WriteReview>()
            WriteReviewScreen(appointmentId = route.appointmentId, shopName = route.shopName)
        }
    }
}


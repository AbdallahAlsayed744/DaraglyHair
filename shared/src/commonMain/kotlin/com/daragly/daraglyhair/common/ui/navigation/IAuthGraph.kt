package com.daragly.daraglyhair.common.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.daragly.daraglyhair.feature.authentication.ui.forgotpassword.ForgotPasswordScreen
import com.daragly.daraglyhair.feature.authentication.ui.login.LoginScreen
import com.daragly.daraglyhair.feature.authentication.ui.register.RegisterScreen
import com.daragly.daraglyhair.feature.authentication.ui.resetpassword.ResetPasswordScreen
import com.daragly.daraglyhair.feature.authentication.ui.splash.SplashScreen
import kotlinx.serialization.Serializable

// ── Destinations ──────────────────────────────────────────────────────────────

sealed interface IAuthGraph : IDestination {
    @Serializable
    data object AuthGraph : IGraph               // graph root
    @Serializable
    data object Splash : IAuthGraph
    @Serializable
    data object Login : IAuthGraph
    @Serializable
    data object Register : IAuthGraph
    @Serializable
    data object ForgotPassword : IAuthGraph
    @Serializable
    data class ResetPassword(val email: String) : IAuthGraph
}

// ── Nav graph builder ─────────────────────────────────────────────────────────

fun NavGraphBuilder.buildAuthNavGraph() {
    navigation<IAuthGraph.AuthGraph>(startDestination = IAuthGraph.Splash) {
        composable<IAuthGraph.Splash> { SplashScreen() }
        composable<IAuthGraph.Login> { LoginScreen() }
        composable<IAuthGraph.Register> { RegisterScreen() }
        composable<IAuthGraph.ForgotPassword> { ForgotPasswordScreen() }
        composable<IAuthGraph.ResetPassword> { backStackEntry ->
            val route = backStackEntry.toRoute<IAuthGraph.ResetPassword>()
            ResetPasswordScreen(email = route.email)
        }
    }
}

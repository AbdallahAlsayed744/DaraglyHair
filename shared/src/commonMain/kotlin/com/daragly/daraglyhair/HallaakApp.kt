package com.daragly.daraglyhair

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import com.daragly.daraglyhair.common.ui.language.ObserveLanguageEvent
import com.daragly.daraglyhair.common.ui.loading.ILoadingEvent
import com.daragly.daraglyhair.common.ui.message.IMessageEvent
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.common.ui.navigation.NavigationEvent
import com.daragly.daraglyhair.common.ui.navigation.buildAuthNavGraph
import com.daragly.daraglyhair.common.ui.navigation.buildMainNavGraph
import com.daragly.daraglyhair.designsystem.components.bars.BottomDestination
import com.daragly.daraglyhair.designsystem.components.bars.HallaakBottomNavBar
import com.daragly.daraglyhair.designsystem.components.dialogs.HallaakLoadingDialog
import com.daragly.daraglyhair.designsystem.components.snackbar.HallaakSnackbar
import com.daragly.daraglyhair.designsystem.components.snackbar.HallaakSnackbarType
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

// ── CompositionLocal for inner scaffold padding ──────────────────────────────
// Mirrors LocalPadding in DelivaryUserApp so nested screens can consume it.
val LocalPadding = compositionLocalOf<PaddingValues> { PaddingValues() }

// ─────────────────────────────────────────────────────────────────────────────
//  HallaakApp — root composable
//
//  Mirrors DelivaryUserApp exactly:
//    • HallaakTheme wrapper
//    • ObserveLanguageEvent → Runtime language switching
//    • ObserveMessageEvent  → Snackbar
//    • ObserveLoadingEvent  → HallaakLoadingDialog
//    • ObserveAsEvents(navigator) → NavHostController
//    • Scaffold (no bottom bar for now)
//    • CompositionLocalProvider(LocalPadding)
//    • NavHost with auth + main graphs
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HallaakApp(
    navigator: INavigator = koinInject(),
    navHostController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    HallaakTheme {

        // ── Global language observer ─────────────────────────────────────
        ObserveLanguageEvent()

        // ── Global message snackbar observer ─────────────────────────────
        ObserveMessageEvent(
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
        )

        // ── Global loading dialog observer ───────────────────────────────
        ObserveLoadingEvent()

        // ── Navigation observer ──────────────────────────────────────────
        ObserveAsEvents(navigator.navigationEvent) { event ->
            when (event) {
                is NavigationEvent.Navigate ->
                    navHostController.navigate(
                        route = event.destination,
                        builder = event.builder,
                    )

                is NavigationEvent.NavigateUp ->
                    navHostController.navigateUp()
            }
        }

        // ── Scaffold with bottom navigation ───────────────────────────
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            snackbarHost = {
                HallaakSnackbarHost(snackbarHostState = snackbarHostState)
            },
            bottomBar = {
                HallaakBottomNavBar(
                    navController = navHostController,
                    destinations = BottomDestination.destinations,
                    useArabicLabels = true,  // TODO: Connect to language preference
                )
            },
        ) { innerPadding ->
            CompositionLocalProvider(LocalPadding provides innerPadding) {
                NavHost(
                    navController = navHostController,
                    startDestination = navigator.startGraph,
                ) {
                    buildAuthNavGraph()
                    buildMainNavGraph()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ObserveMessageEvent
//  Mirrors DelivaryUserApp.ObserveMessageEvent — injects the named event
//  controller and shows a typed HallaakSnackbar.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ObserveMessageEvent(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    val messageEvent: IEventController<IMessageEvent> =
        koinInject(qualifier = named("MessageEvent"))

    ObserveAsEvents(messageEvent.event) { event ->
        when (event) {
            is IMessageEvent.Snackbar ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message = event.message)
                }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ObserveLoadingEvent
//  Mirrors DelivaryUserApp.ObserveLoadingEvent — shows HallaakLoadingDialog
//  while isLoading == true.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ObserveLoadingEvent() {
    val loadingEvent: IEventController<ILoadingEvent> =
        koinInject(qualifier = named("LoadingEvent"))

    var isLoading by remember { mutableStateOf(false) }

    ObserveAsEvents(loadingEvent.event) { event ->
        when (event) {
            is ILoadingEvent.CircularProgressIndicator -> isLoading = event.isLoading
        }
    }

    if (isLoading) HallaakLoadingDialog()
}

// ─────────────────────────────────────────────────────────────────────────────
//  HallaakSnackbarHost
//  Thin wrapper that renders a HallaakSnackbar inside the standard
//  SnackbarHost slot — mirrors DeliveryUserSnackBarHost.
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HallaakSnackbarHost(snackbarHostState: SnackbarHostState) {
    androidx.compose.material3.SnackbarHost(hostState = snackbarHostState) { data ->
        HallaakSnackbar(
            message = data.visuals.message,
            icon = Icons.Outlined.Info,
            type = HallaakSnackbarType.INFO,
        )
    }
}

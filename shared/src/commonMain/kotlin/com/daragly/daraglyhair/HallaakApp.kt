package com.daragly.daraglyhair

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceEnum
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.extension.ObserveAsEvents
import com.daragly.daraglyhair.common.ui.language.ILanguageEvent
import com.daragly.daraglyhair.common.ui.language.LanguageHolder
import com.daragly.daraglyhair.common.ui.language.ObserveLanguageEvent
import com.daragly.daraglyhair.common.ui.loading.ILoadingEvent
import com.daragly.daraglyhair.common.ui.message.IMessageEvent
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.common.ui.navigation.NavigationEvent
import com.daragly.daraglyhair.common.ui.session.ISessionEvent
import com.daragly.daraglyhair.common.ui.navigation.buildAuthNavGraph
import com.daragly.daraglyhair.common.ui.navigation.buildMainNavGraph
import com.daragly.daraglyhair.common.ui.theme.IThemeEvent
import com.daragly.daraglyhair.designsystem.components.bars.BottomDestination
import com.daragly.daraglyhair.designsystem.components.bars.HallaakBottomNavBar
import com.daragly.daraglyhair.designsystem.components.dialogs.HallaakLoadingDialog
import com.daragly.daraglyhair.designsystem.components.snackbar.HallaakSnackbar
import com.daragly.daraglyhair.designsystem.components.snackbar.HallaakSnackbarType
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

// ── CompositionLocal for inner scaffold padding ──────────────────────────────
val LocalPadding = compositionLocalOf<PaddingValues> { PaddingValues() }

// ─────────────────────────────────────────────────────────────────────────────
//  HallaakApp — root composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HallaakApp(
    navigator: INavigator = koinInject(),
    navHostController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val localProvider: ILocalDataSourceProvider = koinInject()
    val languageHolder: LanguageHolder = koinInject()
    val languageEvent: IEventController<ILanguageEvent> =
        koinInject(qualifier = named("LanguageEvent"))
    val systemDark = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(systemDark) }

    // ── Load persisted theme & apply persisted language on first launch ───
    LaunchedEffect(Unit) {
        val savedTheme = localProvider.read(
            key          = LocalDataSourceEnum.THEME,
            defaultValue = AppTheme.SYSTEM.value,
            type         = String::class,
        )
        isDarkTheme = when (AppTheme.fromValue(savedTheme)) {
            AppTheme.LIGHT  -> false
            AppTheme.DARK   -> true
            AppTheme.SYSTEM -> systemDark
        }

        val savedLanguage = localProvider.read(
            key          = LocalDataSourceEnum.LANGUAGE,
            defaultValue = "ar",
            type         = String::class,
        )
        languageHolder.languageCode = savedLanguage
        languageEvent.emit(ILanguageEvent.ChangeLanguage(savedLanguage))
    }

    // ── Observe theme change events for immediate app-wide switching ──────
    val themeEvent: IEventController<IThemeEvent> =
        koinInject(qualifier = named("ThemeEvent"))

    ObserveAsEvents(themeEvent.event) { event ->
        when (event) {
            is IThemeEvent.ChangeTheme -> {
                isDarkTheme = when (event.theme) {
                    AppTheme.LIGHT  -> false
                    AppTheme.DARK   -> true
                    AppTheme.SYSTEM -> systemDark
                }
            }
        }
    }

    HallaakTheme(isDarkTheme = isDarkTheme) {

        ObserveLanguageEvent()

        ObserveMessageEvent(snackbarHostState = snackbarHostState, coroutineScope = coroutineScope)

        ObserveLoadingEvent()

        ObserveSessionExpiredEvent(navigator = navigator)

        ObserveAsEvents(navigator.navigationEvent) { event ->
            when (event) {
                is NavigationEvent.Navigate ->
                    navHostController.navigate(route = event.destination, builder = event.builder)
                is NavigationEvent.NavigateUp ->
                    navHostController.navigateUp()
            }
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            snackbarHost = { HallaakSnackbarHost(snackbarHostState = snackbarHostState) },
            bottomBar = {
                HallaakBottomNavBar(
                    navController   = navHostController,
                    destinations    = BottomDestination.destinations,
                )
            },
        ) { innerPadding ->
            CompositionLocalProvider(LocalPadding provides innerPadding) {
                NavHost(
                    navController    = navHostController,
                    startDestination = navigator.startGraph,
                ) {
                    buildAuthNavGraph()
                    buildMainNavGraph()
                }
            }
        }
    }
}

@Composable
private fun ObserveMessageEvent(snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
    val messageEvent: IEventController<IMessageEvent> = koinInject(qualifier = named("MessageEvent"))
    ObserveAsEvents(messageEvent.event) { event ->
        when (event) {
            is IMessageEvent.Snackbar ->
                coroutineScope.launch { snackbarHostState.showSnackbar(message = event.message) }
        }
    }
}

@Composable
private fun ObserveLoadingEvent() {
    val loadingEvent: IEventController<ILoadingEvent> = koinInject(qualifier = named("LoadingEvent"))
    var isLoading by remember { mutableStateOf(false) }
    ObserveAsEvents(loadingEvent.event) { event ->
        when (event) {
            is ILoadingEvent.CircularProgressIndicator -> isLoading = event.isLoading
        }
    }
    if (isLoading) HallaakLoadingDialog()
}

@Composable
private fun ObserveSessionExpiredEvent(navigator: INavigator) {
    val sessionEvents: IEventController<ISessionEvent> =
        koinInject(qualifier = named("SessionEvent"))
    ObserveAsEvents(sessionEvents.event) { event ->
        when (event) {
            ISessionEvent.Expired -> navigator.navigate(
                destination = IAuthGraph.Login,
                builder = {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                },
            )
        }
    }
}

@Composable
private fun HallaakSnackbarHost(snackbarHostState: SnackbarHostState) {
    androidx.compose.material3.SnackbarHost(hostState = snackbarHostState) { data ->
        HallaakSnackbar(
            message = data.visuals.message,
            icon    = Icons.Outlined.Info,
            type    = HallaakSnackbarType.INFO,
        )
    }
}

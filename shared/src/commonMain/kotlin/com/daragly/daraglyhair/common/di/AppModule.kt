package com.daragly.daraglyhair.common.di

import com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceProvider
import com.daragly.daraglyhair.common.data.repository.remote.ApiService
import com.daragly.daraglyhair.common.data.repository.remote.RemoteDataSourceProvider
import com.daragly.daraglyhair.common.data.repository.remote.SessionManager
import com.daragly.daraglyhair.common.data.repository.remote.httpClientEngineFactory
import com.daragly.daraglyhair.common.data.repository.remote.provideHttpClient
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.common.ui.eventcontroller.EventController
import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.language.ILanguageEvent
import com.daragly.daraglyhair.common.ui.language.LanguageHolder
import com.daragly.daraglyhair.common.ui.loading.ILoadingEvent
import com.daragly.daraglyhair.common.ui.message.IMessageEvent
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.common.ui.navigation.Navigator
import com.daragly.daraglyhair.common.ui.session.ISessionEvent
import com.daragly.daraglyhair.common.ui.theme.IThemeEvent
import com.daragly.daraglyhair.feature.authentication.di.authModule
import com.daragly.daraglyhair.feature.authentication.data.remote.AuthRemoteDataSource
import com.daragly.daraglyhair.feature.bookings.di.bookingsModule
import com.daragly.daraglyhair.feature.explore.di.exploreModule
import com.daragly.daraglyhair.feature.favorites.di.favoritesModule
import com.daragly.daraglyhair.feature.home.di.homeModule
import com.daragly.daraglyhair.feature.settings.di.settingsModule
import com.daragly.daraglyhair.feature.user.di.userModule
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // JSON serializer
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            encodeDefaults = true
        }
    }

    // DataStore (platform-specific factory)
    single { createDataStore() }

    // Local storage
    singleOf(::LocalDataSourceProvider) bind ILocalDataSourceProvider::class

    // Current UI language, kept in memory for the Accept-Language header
    single { LanguageHolder() }

    // Network
    single {
        provideHttpClient(
            engine = httpClientEngineFactory(),
            json = get(),
            publicPaths = listOf(
                // Prefix match, so this also covers REFRESH_ENDPOINT, google and apple login.
                AuthRemoteDataSource.LOGIN_ENDPOINT,
                AuthRemoteDataSource.REGISTER_ENDPOINT,
                AuthRemoteDataSource.FORGOT_PASSWORD_ENDPOINT,
            ),
            getToken = suspend {
                val sessionManager: SessionManager = get()
                try {
                    sessionManager.accessToken()
                } catch (_: Exception) { null }
            },
            getRefreshToken = suspend {
                val sessionManager: SessionManager = get()
                try {
                    sessionManager.refreshToken()
                } catch (_: Exception) { null }
            },
            saveTokens = { accessToken, refreshToken ->
                val sessionManager: SessionManager = get()
                sessionManager.saveSession(accessToken, refreshToken)
            },
            clearTokens = {
                val sessionManager: SessionManager = get()
                sessionManager.clearSession()
            },
            onSessionExpired = {
                val sessionManager: SessionManager = get()
                sessionManager.notifySessionExpired()
            },
            getLang = {
                get<LanguageHolder>().languageCode
            },
        )
    }

    // Session teardown / token cache ownership (needs the client above, so declare it after it)
    single {
        SessionManager(
            client = get(),
            userRepository = get(),
            sessionEvents = get(named("SessionEvent")),
        )
    }

    single { ApiService(get()) }
    singleOf(::RemoteDataSourceProvider) bind IRemoteDataSourceProvider::class

    // Navigator — start from Auth graph
    single<INavigator> { Navigator(startGraph = IAuthGraph.AuthGraph) }

    // Global event buses
    single<IEventController<IMessageEvent>>(qualifier = named("MessageEvent")) { EventController() }
    single<IEventController<ILoadingEvent>>(qualifier = named("LoadingEvent")) { EventController() }
    single<IEventController<ILanguageEvent>>(qualifier = named("LanguageEvent")) { EventController() }
    single<IEventController<IThemeEvent>>(qualifier = named("ThemeEvent")) { EventController() }
    single<IEventController<ISessionEvent>>(qualifier = named("SessionEvent")) { EventController() }

    // Feature modules
    includes(authModule, homeModule, exploreModule, userModule, favoritesModule, settingsModule, bookingsModule)
}


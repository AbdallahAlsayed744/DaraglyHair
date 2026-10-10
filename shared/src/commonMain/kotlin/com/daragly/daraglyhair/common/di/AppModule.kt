package com.daragly.daraglyhair.common.di

import com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceProvider
import com.daragly.daraglyhair.common.data.repository.remote.ApiService
import com.daragly.daraglyhair.common.data.repository.remote.RemoteDataSourceProvider
import com.daragly.daraglyhair.common.data.repository.remote.httpClientEngineFactory
import com.daragly.daraglyhair.common.data.repository.remote.provideHttpClient
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.common.ui.eventcontroller.EventController
import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.language.ILanguageEvent
import com.daragly.daraglyhair.common.ui.loading.ILoadingEvent
import com.daragly.daraglyhair.common.ui.message.IMessageEvent
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.common.ui.navigation.Navigator
import com.daragly.daraglyhair.feature.authentication.di.authModule
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

    // Network
    single {
        provideHttpClient(
            engine = httpClientEngineFactory(),
            json = get(),
            getToken = {
                // Retrieve the stored access token; injected once user service is available
                val localProvider: ILocalDataSourceProvider = get()
                try {
                    localProvider.read(
                        key = com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceEnum.ACCESS_TOKEN,
                        defaultValue = "",
                        type = String::class,
                    ).takeIf { it.isNotBlank() }
                } catch (_: Exception) { null }
            },
            getLang = {
                val localProvider: ILocalDataSourceProvider = get()
                try {
                    localProvider.read(
                        key = com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceEnum.LANGUAGE,
                        defaultValue = "en",
                        type = String::class,
                    )
                } catch (_: Exception) { "en" }
            },
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

    // Feature modules
    includes(authModule)
}

package com.daragly.daraglyhair.feature.favorites.di

import com.daragly.daraglyhair.feature.favorites.data.remote.FavoritesRemoteDataSource
import com.daragly.daraglyhair.feature.favorites.data.repository.FavoritesRepository
import com.daragly.daraglyhair.feature.favorites.domain.interactors.GetFavoriteShopsUseCase
import com.daragly.daraglyhair.feature.favorites.domain.interactors.ToggleFavoriteUseCase
import com.daragly.daraglyhair.feature.favorites.domain.repository.IFavoritesRepository
import com.daragly.daraglyhair.feature.favorites.ui.FavoritesViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val favoritesModule = module {

    // ── Data layer ────────────────────────────────────────────────────────────

    // Dedicated remote data source — uses the shared IRemoteDataSourceProvider
    singleOf(::FavoritesRemoteDataSource)

    // Repository implementation bound to its interface
    singleOf(::FavoritesRepository) bind IFavoritesRepository::class

    // ── Domain layer ──────────────────────────────────────────────────────────

    // Use cases — factories so each caller gets its own instance
    factoryOf(::GetFavoriteShopsUseCase)
    factoryOf(::ToggleFavoriteUseCase)

    // ── Presentation layer ────────────────────────────────────────────────────

    // ViewModel — factory (new instance per screen composition)
    factoryOf(::FavoritesViewModel)
}

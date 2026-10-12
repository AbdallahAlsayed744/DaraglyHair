package com.daragly.daraglyhair.feature.favorites.data.repository

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.favorites.data.remote.FavoritesRemoteDataSource
import com.daragly.daraglyhair.feature.favorites.domain.repository.IFavoritesRepository
import com.daragly.daraglyhair.feature.home.data.mapper.toDomain
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop

/**
 * Concrete implementation of [IFavoritesRepository].
 *
 * Data flow — GET favorites list:
 * ┌──────────────────┐     GET /api/v1/favorites
 * │  FavoritesRepo   │ ──────────────────────────────► API
 * │                  │ ◄──────────────────────────────
 * │  for each shopId │     List<{ id, shopId, createdAt }>
 * │                  │
 * │                  │     GET /api/v1/barbershops/{id}   (per item)
 * │                  │ ──────────────────────────────► API
 * │                  │ ◄──────────────────────────────
 * │                  │     BarbershopDto
 * │                  │
 * │  map to domain   │
 * └──────────────────┘
 *        │
 *        ▼
 *  Resource.Success(List<Barbershop>)   isFavorite = true on every item
 *
 * Data flow — toggle:
 * ┌──────────────────┐     POST /api/v1/favorites/{shopId}/toggle
 * │  FavoritesRepo   │ ──────────────────────────────► API
 * │                  │ ◄──────────────────────────────
 * └──────────────────┘     Boolean (true = added, false = removed)
 */
class FavoritesRepository(
    private val remoteDataSource: FavoritesRemoteDataSource,
) : IFavoritesRepository {

    override suspend fun getFavoriteShops(): Resource<List<Barbershop>> = try {
        val favoriteRecords = remoteDataSource.getFavorites()

        // API confirmed: no favorites exist
        if (favoriteRecords.isEmpty()) {
            return Resource.Success(emptyList())
        }

        // Hydrate each record with full shop details.
        // If one shop fetch fails (e.g. shop deleted on backend), skip it
        // rather than failing the entire list.
        val shops: List<Barbershop> = favoriteRecords.mapNotNull { record ->
            try {
                val dto = remoteDataSource.getShopById(record.shopId)
                dto.toDomain(isFavorite = true)
            } catch (_: Exception) {
                null
            }
        }

        Resource.Success(shops)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun toggleFavorite(shopId: String): Resource<Boolean> = try {
        val isNowFavorite = remoteDataSource.toggleFavorite(shopId)
        Resource.Success(isNowFavorite)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }
}

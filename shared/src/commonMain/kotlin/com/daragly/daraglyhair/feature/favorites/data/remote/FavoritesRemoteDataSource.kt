package com.daragly.daraglyhair.feature.favorites.data.remote

import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.feature.home.data.dto.BarbershopDto
import com.daragly.daraglyhair.feature.home.data.dto.FavoriteDto
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Dedicated remote data source for the Favorites feature.
 *
 * Endpoints:
 *   GET  /api/v1/favorites                    → List<FavoriteDto>
 *   POST /api/v1/favorites/{shopId}/toggle     → Boolean
 *   GET  /api/v1/barbershops/{id}              → BarbershopDto
 *
 * [BarbershopDto] and [FavoriteDto] are reused from the home feature's
 * data layer — no duplication of DTO definitions.
 */
class FavoritesRemoteDataSource(
    private val remote: IRemoteDataSourceProvider,
) {

    /**
     * GET /api/v1/favorites
     * Returns the raw list of favorite records for the authenticated user.
     * Each record contains only { id, shopId, createdAt } — not full shop data.
     */
    suspend fun getFavorites(): List<FavoriteDto> = remote.get(
        endpoint = FAVORITES_ENDPOINT,
        serializer = ListSerializer(FavoriteDto.serializer()),
    )

    /**
     * POST /api/v1/favorites/{shopId}/toggle
     * Toggles the shop in/out of favorites.
     * Returns true  → shop is now a favorite (was added).
     * Returns false → shop is no longer a favorite (was removed).
     */
    suspend fun toggleFavorite(shopId: String): Boolean = remote.post(
        endpoint = "$FAVORITES_ENDPOINT/$shopId/toggle",
        requestBody = null,
        serializer = Boolean.serializer(),
    )

    /**
     * GET /api/v1/barbershops/{id}
     * Fetches the full details of a single barbershop by its ID.
     * Used to hydrate the favorite records that only carry a shopId.
     */
    suspend fun getShopById(shopId: String): BarbershopDto = remote.get(
        endpoint = "$BARBERSHOPS_ENDPOINT/$shopId",
        serializer = BarbershopDto.serializer(),
    )

    companion object {
        const val FAVORITES_ENDPOINT    = "api/v1/favorites"
        const val BARBERSHOPS_ENDPOINT  = "api/v1/barbershops"
    }
}

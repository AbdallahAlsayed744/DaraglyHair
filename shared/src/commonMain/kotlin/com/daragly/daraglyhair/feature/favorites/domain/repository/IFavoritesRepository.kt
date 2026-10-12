package com.daragly.daraglyhair.feature.favorites.domain.repository

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop

/**
 * Contract for the Favorites data layer.
 *
 * Consumed by use cases in the domain layer — never called directly
 * from the ViewModel.
 */
interface IFavoritesRepository {

    /**
     * Fetches the authenticated user's favorite barbershops as fully-hydrated
     * [Barbershop] objects.
     *
     * Internally this calls:
     *   1. GET /api/v1/favorites         → list of { shopId }
     *   2. GET /api/v1/barbershops/{id}  → per shop, to hydrate the record
     *
     * Every returned shop has [Barbershop.isFavorite] = true.
     */
    suspend fun getFavoriteShops(): Resource<List<Barbershop>>

    /**
     * Toggles the favorite status of the given [shopId].
     *
     * Calls: POST /api/v1/favorites/{shopId}/toggle
     *
     * @return true  if the shop is now a favorite (just added)
     *         false if the shop is no longer a favorite (just removed)
     */
    suspend fun toggleFavorite(shopId: String): Resource<Boolean>
}

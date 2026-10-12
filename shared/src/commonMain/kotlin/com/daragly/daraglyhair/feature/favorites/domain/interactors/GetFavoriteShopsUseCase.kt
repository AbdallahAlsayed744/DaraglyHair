package com.daragly.daraglyhair.feature.favorites.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.favorites.domain.repository.IFavoritesRepository
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop

/**
 * Fetches the authenticated user's fully-hydrated list of favorite barbershops.
 *
 * Usage:
 *   val result: Resource<List<Barbershop>> = getFavoriteShopsUseCase(Unit)
 *
 * Returns:
 *   Resource.Success(emptyList())          — user has no favorites
 *   Resource.Success(listOf(shop, …))      — one or more favorites
 *   Resource.Failure(DaraglyHairException) — network / server error
 */
class GetFavoriteShopsUseCase(
    private val repository: IFavoritesRepository,
) : BaseUseCase<Resource<List<Barbershop>>, Unit>() {

    override suspend fun invoke(body: Unit): Resource<List<Barbershop>> =
        repository.getFavoriteShops()
}

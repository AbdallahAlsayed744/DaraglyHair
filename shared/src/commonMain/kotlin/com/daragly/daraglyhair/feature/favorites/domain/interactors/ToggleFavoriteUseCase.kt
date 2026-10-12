package com.daragly.daraglyhair.feature.favorites.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.favorites.domain.repository.IFavoritesRepository

/**
 * Toggles the favorite status of a barbershop.
 *
 * Usage:
 *   val result: Resource<Boolean> = toggleFavoriteUseCase(shopId)
 *
 * Returns:
 *   Resource.Success(true)                 — shop was added to favorites
 *   Resource.Success(false)                — shop was removed from favorites
 *   Resource.Failure(DaraglyHairException) — network / server error
 *
 * The caller is responsible for preventing duplicate concurrent calls
 * for the same shopId — the use case itself is stateless.
 */
class ToggleFavoriteUseCase(
    private val repository: IFavoritesRepository,
) : BaseUseCase<Resource<Boolean>, String>() {

    /** @param body the shopId to toggle */
    override suspend fun invoke(body: String): Resource<Boolean> =
        repository.toggleFavorite(body)
}

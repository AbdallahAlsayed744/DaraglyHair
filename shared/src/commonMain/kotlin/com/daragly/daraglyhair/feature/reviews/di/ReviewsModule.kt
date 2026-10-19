package com.daragly.daraglyhair.feature.reviews.di

import com.daragly.daraglyhair.feature.reviews.data.remote.ReviewsRemoteDataSource
import com.daragly.daraglyhair.feature.reviews.data.repository.ReviewsRepository
import com.daragly.daraglyhair.feature.reviews.domain.interactors.CreateReviewUseCase
import com.daragly.daraglyhair.feature.reviews.domain.interactors.GetShopReviewsUseCase
import com.daragly.daraglyhair.feature.reviews.domain.repository.IReviewsRepository
import com.daragly.daraglyhair.feature.reviews.ui.ShopReviewsViewModel
import com.daragly.daraglyhair.feature.reviews.ui.WriteReviewViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val reviewsModule = module {

    // ── Data layer ────────────────────────────────────────────────────────────
    singleOf(::ReviewsRemoteDataSource)
    singleOf(::ReviewsRepository) bind IReviewsRepository::class

    // ── Domain layer ──────────────────────────────────────────────────────────
    factoryOf(::GetShopReviewsUseCase)
    factoryOf(::CreateReviewUseCase)

    // ── Presentation layer (parameterized factories, consumed via parametersOf) ──
    factory { (shopId: String) ->
        ShopReviewsViewModel(
            shopId = shopId,
            getShopReviewsUseCase = get(),
        )
    }

    factory { (appointmentId: String) ->
        WriteReviewViewModel(
            appointmentId = appointmentId,
            createReviewUseCase = get(),
        )
    }
}

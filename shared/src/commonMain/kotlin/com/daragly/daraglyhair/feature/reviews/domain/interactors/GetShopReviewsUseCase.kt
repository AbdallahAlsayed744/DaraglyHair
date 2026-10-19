package com.daragly.daraglyhair.feature.reviews.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.reviews.domain.model.Review
import com.daragly.daraglyhair.feature.reviews.domain.model.ShopReviewsQuery
import com.daragly.daraglyhair.feature.reviews.domain.repository.IReviewsRepository

class GetShopReviewsUseCase(
    private val repository: IReviewsRepository,
) : BaseUseCase<Resource<List<Review>>, ShopReviewsQuery>() {

    override suspend fun invoke(body: ShopReviewsQuery): Resource<List<Review>> =
        repository.getShopReviews(body)
}

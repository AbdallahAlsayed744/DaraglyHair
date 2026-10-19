package com.daragly.daraglyhair.feature.reviews.domain.repository

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.reviews.domain.model.Review
import com.daragly.daraglyhair.feature.reviews.domain.model.ShopReviewsQuery
import com.daragly.daraglyhair.feature.reviews.domain.model.SubmitReview

interface IReviewsRepository {
    /** One page of a shop's reviews. */
    suspend fun getShopReviews(query: ShopReviewsQuery): Resource<List<Review>>

    /** Creates a review for a completed appointment, then attaches its photos. */
    suspend fun createReview(submit: SubmitReview): Resource<Unit>
}

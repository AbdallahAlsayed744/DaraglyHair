package com.daragly.daraglyhair.feature.reviews.data.remote

import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.feature.home.data.dto.PaginatedResponse
import com.daragly.daraglyhair.feature.reviews.data.dto.ReviewDto
import com.daragly.daraglyhair.feature.reviews.data.request.AddReviewImageApiRequest
import com.daragly.daraglyhair.feature.reviews.data.request.CreateReviewApiRequest
import com.daragly.daraglyhair.feature.reviews.data.request.ReviewsSearchApiRequest
import kotlinx.serialization.builtins.serializer

class ReviewsRemoteDataSource(private val remote: IRemoteDataSourceProvider) {

    // POST /api/v1/reviews/search → paginated reviews for a shop
    suspend fun searchReviews(request: ReviewsSearchApiRequest): PaginatedResponse<ReviewDto> =
        remote.post(
            endpoint = REVIEWS_SEARCH_ENDPOINT,
            requestBody = request,
            serializer = PaginatedResponse.serializer(ReviewDto.serializer()),
        )

    // POST /api/v1/reviews → creates a review; returns the new review id (bare GUID string)
    suspend fun createReview(request: CreateReviewApiRequest): String = remote.post(
        endpoint = REVIEWS_ENDPOINT,
        requestBody = request,
        serializer = String.serializer(),
    )

    // POST /api/v1/reviews/{id}/images → attaches one photo
    suspend fun addReviewImage(reviewId: String, request: AddReviewImageApiRequest): String = remote.post(
        endpoint = "$REVIEWS_ENDPOINT/$reviewId/images",
        requestBody = request,
        serializer = String.serializer(),
    )

    companion object {
        const val REVIEWS_ENDPOINT = "api/v1/reviews"
        const val REVIEWS_SEARCH_ENDPOINT = "api/v1/reviews/search"
    }
}

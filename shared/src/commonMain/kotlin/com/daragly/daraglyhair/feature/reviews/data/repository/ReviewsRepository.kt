package com.daragly.daraglyhair.feature.reviews.data.repository

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.reviews.data.mapper.toDomain
import com.daragly.daraglyhair.feature.reviews.data.remote.ReviewsRemoteDataSource
import com.daragly.daraglyhair.feature.reviews.data.request.AddReviewImageApiRequest
import com.daragly.daraglyhair.feature.reviews.data.request.CreateReviewApiRequest
import com.daragly.daraglyhair.feature.reviews.data.request.ReviewsSearchApiRequest
import com.daragly.daraglyhair.feature.reviews.domain.model.Review
import com.daragly.daraglyhair.feature.reviews.domain.model.ShopReviewsQuery
import com.daragly.daraglyhair.feature.reviews.domain.model.SubmitReview
import com.daragly.daraglyhair.feature.reviews.domain.repository.IReviewsRepository
import com.daragly.daraglyhair.feature.user.data.dto.FileUploadRequestDto

class ReviewsRepository(
    private val remoteDataSource: ReviewsRemoteDataSource,
) : IReviewsRepository {

    override suspend fun getShopReviews(query: ShopReviewsQuery): Resource<List<Review>> = try {
        val response = remoteDataSource.searchReviews(
            ReviewsSearchApiRequest(
                pageNumber = query.pageNumber,
                pageSize = query.pageSize,
                shopId = query.shopId,
                shopBarberId = query.shopBarberId,
            ),
        )
        Resource.Success(response.getItemsList().map { it.toDomain() })
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun createReview(submit: SubmitReview): Resource<Unit> = try {
        val rawId = remoteDataSource.createReview(
            CreateReviewApiRequest(
                appointmentId = submit.appointmentId,
                overallRating = submit.overallRating,
                qualityRating = submit.qualityRating,
                cleanlinessRating = submit.cleanlinessRating,
                valueRating = submit.valueRating,
                comment = submit.comment?.takeIf { it.isNotBlank() },
            ),
        )
        val reviewId = rawId.trim().trim('"')
        // Photos attach to the review id; skip if the endpoint didn't hand back a usable id.
        if (reviewId.isNotBlank() && !reviewId.startsWith("{")) {
            submit.photos.forEach { photo ->
                remoteDataSource.addReviewImage(
                    reviewId = reviewId,
                    request = AddReviewImageApiRequest(
                        reviewId = reviewId,
                        image = FileUploadRequestDto(
                            name = photo.fileName,
                            extension = photo.extension,
                            data = photo.dataBase64,
                        ),
                    ),
                )
            }
        }
        Resource.Success(Unit)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }
}

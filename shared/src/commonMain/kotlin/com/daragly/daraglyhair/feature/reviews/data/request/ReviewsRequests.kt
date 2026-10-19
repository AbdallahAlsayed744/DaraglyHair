package com.daragly.daraglyhair.feature.reviews.data.request

import com.daragly.daraglyhair.feature.user.data.dto.FileUploadRequestDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** POST /api/v1/reviews/search body. */
@Serializable
data class ReviewsSearchApiRequest(
    @SerialName("pageNumber")
    val pageNumber: Int = 1,
    @SerialName("pageSize")
    val pageSize: Int = 10,
    @SerialName("shopId")
    val shopId: String,
    @SerialName("shopBarberId")
    val shopBarberId: String? = null,
)

/** POST /api/v1/reviews body — all four ratings are 1..5. */
@Serializable
data class CreateReviewApiRequest(
    @SerialName("appointmentId")
    val appointmentId: String,
    @SerialName("overallRating")
    val overallRating: Int,
    @SerialName("qualityRating")
    val qualityRating: Int,
    @SerialName("cleanlinessRating")
    val cleanlinessRating: Int,
    @SerialName("valueRating")
    val valueRating: Int,
    @SerialName("comment")
    val comment: String? = null,
)

/** POST /api/v1/reviews/{id}/images body. */
@Serializable
data class AddReviewImageApiRequest(
    @SerialName("reviewId")
    val reviewId: String,
    @SerialName("image")
    val image: FileUploadRequestDto,
)

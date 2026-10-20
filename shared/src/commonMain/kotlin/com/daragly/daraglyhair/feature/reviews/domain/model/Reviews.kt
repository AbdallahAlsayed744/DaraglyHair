package com.daragly.daraglyhair.feature.reviews.domain.model

/** A published shop review. [photoUrls] holds the attached remote photos (may be empty). */
data class Review(
    val id: String,
    val shopId: String?,
    val appointmentId: String?,
    val overallRating: Int,
    val qualityRating: Int?,
    val cleanlinessRating: Int?,
    val valueRating: Int?,
    val comment: String?,
    val photoUrls: List<String> = emptyList(),
    val createdOn: String?,
)

/** Parameters for a shop's (paginated) reviews list. */
data class ShopReviewsQuery(
    val shopId: String,
    val pageNumber: Int = 1,
    val pageSize: Int = 10,
    val shopBarberId: String? = null,
)

/** A picked photo, base64-ready for upload via FileUploadRequest. */
data class ReviewPhoto(
    val fileName: String,
    val extension: String,
    val dataBase64: String,
)

/** Everything needed to publish a review for a completed appointment. */
data class SubmitReview(
    val appointmentId: String,
    val overallRating: Int,
    val qualityRating: Int,
    val cleanlinessRating: Int,
    val valueRating: Int,
    val comment: String?,
    val photos: List<ReviewPhoto>,
)

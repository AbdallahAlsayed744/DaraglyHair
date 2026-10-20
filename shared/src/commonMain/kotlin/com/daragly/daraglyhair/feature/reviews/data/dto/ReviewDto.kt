package com.daragly.daraglyhair.feature.reviews.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A single shop review returned by POST /api/v1/reviews/search.
 * Field names follow the API docs; the response body itself isn't fully specified,
 * so non-key fields are nullable and the client uses ignoreUnknownKeys.
 */
@Serializable
data class ReviewDto(
    @SerialName("id")
    val id: String,
    @SerialName("appointmentId")
    val appointmentId: String? = null,
    @SerialName("shopId")
    val shopId: String? = null,
    @SerialName("shopBarberId")
    val shopBarberId: String? = null,
    @SerialName("overallRating")
    val overallRating: Int = 0,
    @SerialName("qualityRating")
    val qualityRating: Int? = null,
    @SerialName("cleanlinessRating")
    val cleanlinessRating: Int? = null,
    @SerialName("valueRating")
    val valueRating: Int? = null,
    @SerialName("comment")
    val comment: String? = null,
    /** Attached photo objects (max 5). */
    @SerialName("images")
    val images: List<ReviewImageDto>? = null,
    @SerialName("createdOn")
    val createdOn: String? = null,
)

@Serializable
data class ReviewImageDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("path")
    val path: String? = null,
) {
    /** First non-blank URL across the candidate field names the backend may use. */
    fun resolvedUrl(): String? =
        listOf(url, imageUrl, path)
            .firstOrNull { !it.isNullOrBlank() }
}

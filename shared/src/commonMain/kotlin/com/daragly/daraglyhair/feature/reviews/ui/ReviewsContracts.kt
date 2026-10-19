package com.daragly.daraglyhair.feature.reviews.ui

import com.daragly.daraglyhair.feature.reviews.domain.model.Review
import com.daragly.daraglyhair.feature.reviews.domain.model.ReviewPhoto

// ── Shop reviews list ─────────────────────────────────────────────────────────

data class ShopReviewsState(
    val content: ShopReviewsContent = ShopReviewsContent.Loading,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
)

sealed class ShopReviewsContent {
    data object Loading : ShopReviewsContent()
    data class Success(val reviews: List<Review>) : ShopReviewsContent()
    data object Empty : ShopReviewsContent()
    data class Error(val message: String) : ShopReviewsContent()
}

sealed interface ShopReviewsAction {
    data object Retry : ShopReviewsAction
    data object LoadMore : ShopReviewsAction
}

// ── Write review ──────────────────────────────────────────────────────────────

/** The four rating categories, each 1..5. */
enum class RatingCategory { OVERALL, QUALITY, CLEANLINESS, VALUE }

data class WriteReviewState(
    val ratings: Map<RatingCategory, Int> = emptyMap(),
    val comment: String = "",
    val photos: List<ReviewPhoto> = emptyList(),
    val isSubmitting: Boolean = false,
) {
    val canSubmit: Boolean
        get() = !isSubmitting && RatingCategory.entries.all { (ratings[it] ?: 0) >= 1 }
}

sealed interface WriteReviewAction {
    data class RatingChanged(val category: RatingCategory, val value: Int) : WriteReviewAction
    data class CommentChanged(val value: String) : WriteReviewAction
    data class PhotoAdded(val photo: ReviewPhoto) : WriteReviewAction
    data class PhotoRemoved(val photo: ReviewPhoto) : WriteReviewAction
    data object Submit : WriteReviewAction
}

sealed interface WriteReviewEvent {
    data object Submitted : WriteReviewEvent
}

package com.daragly.daraglyhair.feature.reviews.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.reviews.domain.interactors.CreateReviewUseCase
import com.daragly.daraglyhair.feature.reviews.domain.model.SubmitReview
import kotlinx.coroutines.launch

class WriteReviewViewModel(
    private val appointmentId: String,
    private val createReviewUseCase: CreateReviewUseCase,
) : BaseViewModel<WriteReviewState, WriteReviewAction, WriteReviewEvent>(WriteReviewState()) {

    override fun onAction(action: WriteReviewAction) {
        when (action) {
            is WriteReviewAction.RatingChanged -> updateState {
                copy(ratings = ratings + (action.category to action.value.coerceIn(1, 5)))
            }

            is WriteReviewAction.CommentChanged -> updateState { copy(comment = action.value) }

            is WriteReviewAction.PhotoAdded -> updateState {
                if (photos.size >= MAX_PHOTOS) this else copy(photos = photos + action.photo)
            }

            is WriteReviewAction.PhotoRemoved -> updateState {
                copy(photos = photos - action.photo)
            }

            WriteReviewAction.Submit -> submit()
        }
    }

    private fun submit() {
        val s = state.value
        if (!s.canSubmit) return
        updateState { copy(isSubmitting = true) }
        viewModelScope.launch {
            val result = createReviewUseCase(
                SubmitReview(
                    appointmentId = appointmentId,
                    overallRating = s.ratings.getValue(RatingCategory.OVERALL),
                    qualityRating = s.ratings.getValue(RatingCategory.QUALITY),
                    cleanlinessRating = s.ratings.getValue(RatingCategory.CLEANLINESS),
                    valueRating = s.ratings.getValue(RatingCategory.VALUE),
                    comment = s.comment.takeIf { it.isNotBlank() },
                    photos = s.photos,
                ),
            )
            when (result) {
                is Resource.Success -> {
                    updateState { copy(isSubmitting = false) }
                    emitEvent(WriteReviewEvent.Submitted)
                }
                is Resource.Failure -> {
                    updateState { copy(isSubmitting = false) }
                    onGlobalError(result.exception.message.orEmpty())
                }
                is Resource.Loading -> { /* flag set above */ }
            }
        }
    }

    companion object {
        const val MAX_PHOTOS = 5
    }
}

package com.daragly.daraglyhair.feature.reviews.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.reviews.domain.model.SubmitReview
import com.daragly.daraglyhair.feature.reviews.domain.repository.IReviewsRepository

class CreateReviewUseCase(
    private val repository: IReviewsRepository,
) : BaseUseCase<Resource<Unit>, SubmitReview>() {

    override suspend fun invoke(body: SubmitReview): Resource<Unit> =
        repository.createReview(body)
}

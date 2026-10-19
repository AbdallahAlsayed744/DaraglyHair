package com.daragly.daraglyhair.feature.reviews.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.reviews.domain.interactors.GetShopReviewsUseCase
import com.daragly.daraglyhair.feature.reviews.domain.model.Review
import com.daragly.daraglyhair.feature.reviews.domain.model.ShopReviewsQuery
import kotlinx.coroutines.launch

class ShopReviewsViewModel(
    private val shopId: String,
    private val getShopReviewsUseCase: GetShopReviewsUseCase,
) : BaseViewModel<ShopReviewsState, ShopReviewsAction, Unit>(ShopReviewsState()) {

    private val pageSize = 10
    private var currentPage = 0
    private val accumulated = mutableListOf<Review>()

    init {
        loadFirstPage()
    }

    override fun onAction(action: ShopReviewsAction) {
        when (action) {
            ShopReviewsAction.Retry -> loadFirstPage()
            ShopReviewsAction.LoadMore -> loadNextPage()
        }
    }

    private fun loadFirstPage() {
        updateState { copy(content = ShopReviewsContent.Loading, isLoadingMore = false) }
        accumulated.clear()
        currentPage = 1
        fetchPage(replace = true)
    }

    private fun loadNextPage() {
        if (state.value.isLoadingMore || !state.value.canLoadMore) return
        updateState { copy(isLoadingMore = true) }
        currentPage += 1
        fetchPage(replace = false)
    }

    private fun fetchPage(replace: Boolean) {
        viewModelScope.launch {
            when (val result = getShopReviewsUseCase(ShopReviewsQuery(shopId, currentPage, pageSize))) {
                is Resource.Success -> {
                    val page = result.model
                    if (replace) {
                        accumulated.clear()
                        accumulated.addAll(page)
                    } else {
                        accumulated.addAll(page)
                    }
                    val list = accumulated.toList()
                    updateState {
                        copy(
                            content = if (list.isEmpty()) ShopReviewsContent.Empty else ShopReviewsContent.Success(list),
                            isLoadingMore = false,
                            canLoadMore = page.size == pageSize,
                        )
                    }
                }
                is Resource.Failure -> {
                    updateState {
                        copy(
                            content = if (replace) {
                                ShopReviewsContent.Error(result.exception.message.orEmpty())
                            } else {
                                content
                            },
                            isLoadingMore = false,
                        )
                    }
                }
                is Resource.Loading -> { /* handled via state flags */ }
            }
        }
    }
}

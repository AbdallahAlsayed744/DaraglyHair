package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetShopServicesUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraftStateHolder
import kotlinx.coroutines.launch

class BookingServicesViewModel(
    private val shopId: String,
    private val getShopServicesUseCase: GetShopServicesUseCase,
    private val draftHolder: BookingDraftStateHolder,
) : BaseViewModel<BookingServicesState, BookingServicesAction, BookingServicesEvent>(BookingServicesState()) {

    init {
        // Defensive: keeps selections when resuming this shop's flow, resets otherwise.
        draftHolder.start(shopId, null)
        loadServices()
    }

    override fun onAction(action: BookingServicesAction) {
        when (action) {
            is BookingServicesAction.Retry -> loadServices()
            is BookingServicesAction.ToggleService -> toggle(action)
            is BookingServicesAction.ContinueClicked -> onContinueClicked()
        }
    }

    private fun loadServices() {
        updateState { copy(services = SectionState.Loading) }
        viewModelScope.launch {
            when (val result = getShopServicesUseCase(shopId)) {
                is Resource.Success -> {
                    val active = result.model.filter { it.isActive }
                    // Preserve prior selections when revisiting the step for the same shop.
                    val previous = draftHolder.current.selectedServices
                        .filter { service -> active.any { it.shopServiceId == service.shopServiceId } }
                        .map { it.shopServiceId }
                        .toSet()
                    updateState {
                        copy(
                            services = SectionState.Data(active),
                            selectedServiceIds = previous,
                        )
                    }
                }
                is Resource.Failure -> updateState {
                    copy(services = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun toggle(action: BookingServicesAction.ToggleService) {
        if (!action.service.isActive) return
        updateState {
            val next = selectedServiceIds.toMutableSet()
            if (!next.add(action.service.shopServiceId)) next.remove(action.service.shopServiceId)
            copy(selectedServiceIds = next)
        }
    }

    private fun onContinueClicked() {
        val selected = state.value.selectedServices
        if (selected.isEmpty()) return
        draftHolder.selectServices(selected)
        emitEvent(BookingServicesEvent.NavigateToDateTime(shopId))
    }
}

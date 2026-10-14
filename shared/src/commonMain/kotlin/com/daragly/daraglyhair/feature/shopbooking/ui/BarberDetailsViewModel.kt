package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.home.domain.repository.IHomeRepository
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetBarberProfileUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetShopBarbersUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraftStateHolder
import kotlinx.coroutines.launch

class BarberDetailsViewModel(
    private val shopId: String,
    private val barberId: String,
    private val homeRepository: IHomeRepository,
    private val getBarberProfileUseCase: GetBarberProfileUseCase,
    private val getShopBarbersUseCase: GetShopBarbersUseCase,
    private val draftHolder: BookingDraftStateHolder,
) : BaseViewModel<BarberDetailsState, BarberDetailsAction, BarberDetailsEvent>(BarberDetailsState()) {

    init {
        loadBarber()
        loadShopContext()
        loadRelationship()
    }

    override fun onAction(action: BarberDetailsAction) {
        when (action) {
            is BarberDetailsAction.Retry -> {
                loadBarber()
                loadShopContext()
                loadRelationship()
            }
            is BarberDetailsAction.BookClicked -> onBookClicked()
        }
    }

    private fun loadBarber() {
        updateState { copy(barber = SectionState.Loading) }
        viewModelScope.launch {
            when (val result = getBarberProfileUseCase(barberId)) {
                is Resource.Success -> updateState { copy(barber = SectionState.Data(result.model)) }
                is Resource.Failure -> updateState {
                    copy(barber = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadShopContext() {
        updateState { copy(shop = SectionState.Loading) }
        viewModelScope.launch {
            when (val result = homeRepository.getBarbershopById(shopId)) {
                is Resource.Success -> updateState { copy(shop = SectionState.Data(result.model)) }
                is Resource.Failure -> updateState {
                    copy(shop = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    /** Re-reads the shop-barber relationship so booking uses the authoritative shopBarberId and flags. */
    private fun loadRelationship() {
        viewModelScope.launch {
            when (val result = getShopBarbersUseCase(shopId)) {
                is Resource.Success -> updateState {
                    copy(shopBarber = result.model.firstOrNull { it.barberId == barberId })
                }
                is Resource.Failure -> Unit // CTA gating falls back to profile flags
                is Resource.Loading -> Unit
            }
        }
    }

    private fun onBookClicked() {
        if (state.value.bookBlockReason != null) return
        val relationship = state.value.shopBarber ?: return
        draftHolder.start(shopId = shopId, shopName = state.value.shopData?.name)
        draftHolder.selectBarber(
            shopBarberId = relationship.shopBarberId,
            barberId = barberId,
            specialization = state.value.barberData?.specialization,
        )
        emitEvent(BarberDetailsEvent.NavigateToBookingServices(shopId))
    }
}

package com.daragly.daraglyhair.feature.shopbooking.ui

import androidx.lifecycle.viewModelScope
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.home.domain.repository.IHomeRepository
import com.daragly.daraglyhair.feature.reviews.domain.interactors.GetShopReviewsUseCase
import com.daragly.daraglyhair.feature.reviews.domain.model.ShopReviewsQuery
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetBarberProfileUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetShopBarbersUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetShopServicesUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraftStateHolder
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BarberProfile
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopBarber
import kotlinx.coroutines.launch

class ShopDetailsViewModel(
    private val shopId: String,
    private val homeRepository: IHomeRepository,
    private val getShopBarbersUseCase: GetShopBarbersUseCase,
    private val getShopServicesUseCase: GetShopServicesUseCase,
    private val getBarberProfileUseCase: GetBarberProfileUseCase,
    private val getShopReviewsUseCase: GetShopReviewsUseCase,
    private val draftHolder: BookingDraftStateHolder,
) : BaseViewModel<ShopDetailsState, ShopDetailsAction, ShopDetailsEvent>(ShopDetailsState()) {

    init {
        loadShop()
        loadServices()
        loadBarbers()
        loadReviews()
    }

    override fun onAction(action: ShopDetailsAction) {
        when (action) {
            is ShopDetailsAction.RetryShop -> loadShop()
            is ShopDetailsAction.RetryServices -> loadServices()
            is ShopDetailsAction.RetryBarbers -> loadBarbers()
            is ShopDetailsAction.RetryReviews -> loadReviews()
            is ShopDetailsAction.BarberClicked ->
                emitEvent(ShopDetailsEvent.NavigateToBarber(shopId, action.shopBarber.barberId))
            is ShopDetailsAction.BookClicked -> onBookClicked()
            is ShopDetailsAction.SeeAllReviewsClicked ->
                emitEvent(ShopDetailsEvent.NavigateToShopReviews(shopId))
        }
    }

    // ── Sections ──────────────────────────────────────────────────────────────

    private fun loadShop() {
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

    private fun loadServices() {
        updateState { copy(services = SectionState.Loading) }
        viewModelScope.launch {
            when (val result = getShopServicesUseCase(shopId)) {
                is Resource.Success -> updateState { copy(services = SectionState.Data(result.model)) }
                is Resource.Failure -> updateState {
                    copy(services = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadBarbers() {
        updateState { copy(barbers = SectionState.Loading) }
        viewModelScope.launch {
            when (val result = getShopBarbersUseCase(shopId)) {
                is Resource.Success -> {
                    val barbers = result.model.filter { it.isActive }
                    val profiles = fetchProfiles(barbers)
                    updateState {
                        copy(
                            barbers = SectionState.Data(barbers),
                            barberProfiles = profiles,
                        )
                    }
                }
                is Resource.Failure -> updateState {
                    copy(barbers = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun loadReviews() {
        updateState { copy(reviews = SectionState.Loading) }
        viewModelScope.launch {
            when (val result = getShopReviewsUseCase(ShopReviewsQuery(shopId, pageNumber = 1, pageSize = 3))) {
                is Resource.Success -> updateState { copy(reviews = SectionState.Data(result.model)) }
                is Resource.Failure -> updateState {
                    copy(reviews = SectionState.Error(result.exception.message.orEmpty()))
                }
                is Resource.Loading -> Unit
            }
        }
    }

    /** Best-effort profile lookup so barber rows can show specialization; failures degrade to null. */
    private suspend fun fetchProfiles(barbers: List<ShopBarber>): Map<String, BarberProfile> {
        val profiles = mutableMapOf<String, BarberProfile>()
        for (barber in barbers) {
            try {
                val result = getBarberProfileUseCase(barber.barberId)
                if (result is Resource.Success) profiles[barber.barberId] = result.model
            } catch (_: Exception) {
                // Non-fatal: profile display falls back to a generic label.
            }
        }
        return profiles
    }

    // ── Booking entry ─────────────────────────────────────────────────────────

    private fun onBookClicked() {
        val shop = state.value.shopData ?: return
        if (!state.value.canBookOnline) return // CTA is disabled in UI — guard only
        draftHolder.start(shopId = shop.id, shopName = shop.name)
        draftHolder.selectBarber(shopBarberId = null, barberId = null, specialization = null)
        emitEvent(ShopDetailsEvent.NavigateToBookingServices(shopId))
    }
}

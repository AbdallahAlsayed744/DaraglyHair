package com.daragly.daraglyhair.feature.shopbooking.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotOffer
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotQuery
import com.daragly.daraglyhair.feature.shopbooking.domain.repository.IShopBookingRepository

class GetAvailableSlotsUseCase(
    private val repository: IShopBookingRepository,
) : BaseUseCase<Resource<SlotOffer>, SlotQuery>() {

    override suspend fun invoke(body: SlotQuery): Resource<SlotOffer> =
        repository.getAvailableSlots(query = body)
}

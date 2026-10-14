package com.daragly.daraglyhair.feature.shopbooking.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BarberProfile
import com.daragly.daraglyhair.feature.shopbooking.domain.repository.IShopBookingRepository

class GetBarberProfileUseCase(
    private val repository: IShopBookingRepository,
) : BaseUseCase<Resource<BarberProfile>, String>() {

    override suspend fun invoke(body: String): Resource<BarberProfile> =
        repository.getBarberProfile(barberId = body)
}

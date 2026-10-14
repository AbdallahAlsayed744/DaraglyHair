package com.daragly.daraglyhair.feature.shopbooking.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopBarber
import com.daragly.daraglyhair.feature.shopbooking.domain.repository.IShopBookingRepository

class GetShopBarbersUseCase(
    private val repository: IShopBookingRepository,
) : BaseUseCase<Resource<List<ShopBarber>>, String>() {

    override suspend fun invoke(body: String): Resource<List<ShopBarber>> =
        repository.getShopBarbers(shopId = body)
}

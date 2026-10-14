package com.daragly.daraglyhair.feature.shopbooking.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopService
import com.daragly.daraglyhair.feature.shopbooking.domain.repository.IShopBookingRepository

class GetShopServicesUseCase(
    private val repository: IShopBookingRepository,
) : BaseUseCase<Resource<List<ShopService>>, String>() {

    override suspend fun invoke(body: String): Resource<List<ShopService>> =
        repository.getShopServices(shopId = body)
}

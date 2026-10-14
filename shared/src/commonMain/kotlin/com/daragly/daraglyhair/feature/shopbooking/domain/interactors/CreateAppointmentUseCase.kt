package com.daragly.daraglyhair.feature.shopbooking.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingSubmission
import com.daragly.daraglyhair.feature.shopbooking.domain.repository.IShopBookingRepository

class CreateAppointmentUseCase(
    private val repository: IShopBookingRepository,
) : BaseUseCase<Resource<String>, BookingSubmission>() {

    override suspend fun invoke(body: BookingSubmission): Resource<String> =
        repository.createAppointment(submission = body)
}

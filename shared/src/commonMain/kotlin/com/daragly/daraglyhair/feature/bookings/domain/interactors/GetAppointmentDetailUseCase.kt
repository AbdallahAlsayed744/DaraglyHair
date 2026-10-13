package com.daragly.daraglyhair.feature.bookings.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.repository.IBookingsRepository

class GetAppointmentDetailUseCase(
    private val repository: IBookingsRepository,
) : BaseUseCase<Resource<BookingAppointment>, String>() {

    override suspend fun invoke(body: String): Resource<BookingAppointment> =
        repository.getAppointmentById(id = body)
}

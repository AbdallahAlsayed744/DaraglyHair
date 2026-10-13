package com.daragly.daraglyhair.feature.bookings.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingTabFilter
import com.daragly.daraglyhair.feature.bookings.domain.repository.IBookingsRepository

class GetAppointmentsUseCase(
    private val repository: IBookingsRepository,
) : BaseUseCase<Resource<List<BookingAppointment>>, BookingTabFilter>() {

    override suspend fun invoke(body: BookingTabFilter): Resource<List<BookingAppointment>> =
        repository.getAppointments(tab = body)
}

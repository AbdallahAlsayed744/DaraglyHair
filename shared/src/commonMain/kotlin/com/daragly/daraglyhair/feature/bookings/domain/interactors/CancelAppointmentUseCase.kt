package com.daragly.daraglyhair.feature.bookings.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.bookings.domain.repository.IBookingsRepository

class CancelAppointmentUseCase(
    private val repository: IBookingsRepository,
) : BaseUseCase<Resource<Unit>, String>() {

    override suspend fun invoke(body: String): Resource<Unit> =
        repository.cancelAppointment(id = body)
}

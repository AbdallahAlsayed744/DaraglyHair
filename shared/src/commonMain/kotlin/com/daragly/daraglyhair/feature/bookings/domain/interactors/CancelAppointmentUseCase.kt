package com.daragly.daraglyhair.feature.bookings.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.bookings.domain.model.CancellationRequest
import com.daragly.daraglyhair.feature.bookings.domain.repository.IBookingsRepository

class CancelAppointmentUseCase(
    private val repository: IBookingsRepository,
) : BaseUseCase<Resource<Unit>, CancellationRequest>() {

    override suspend fun invoke(body: CancellationRequest): Resource<Unit> =
        repository.cancelAppointment(request = body)
}

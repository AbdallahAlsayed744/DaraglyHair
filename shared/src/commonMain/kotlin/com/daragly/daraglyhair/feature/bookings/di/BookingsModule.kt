package com.daragly.daraglyhair.feature.bookings.di

import com.daragly.daraglyhair.feature.bookings.data.remote.BookingsRemoteDataSource
import com.daragly.daraglyhair.feature.bookings.data.repository.BookingsRepository
import com.daragly.daraglyhair.feature.bookings.domain.interactors.CancelAppointmentUseCase
import com.daragly.daraglyhair.feature.bookings.domain.interactors.GetAppointmentDetailUseCase
import com.daragly.daraglyhair.feature.bookings.domain.interactors.GetAppointmentsUseCase
import com.daragly.daraglyhair.feature.bookings.domain.repository.IBookingsRepository
import com.daragly.daraglyhair.feature.bookings.ui.AppointmentDetailViewModel
import com.daragly.daraglyhair.feature.bookings.ui.BookingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val bookingsModule = module {

    // ── Data layer ────────────────────────────────────────────────────────────

    singleOf(::BookingsRemoteDataSource)
    singleOf(::BookingsRepository) bind IBookingsRepository::class

    // ── Domain layer ──────────────────────────────────────────────────────────

    factoryOf(::GetAppointmentsUseCase)
    factoryOf(::GetAppointmentDetailUseCase)
    factoryOf(::CancelAppointmentUseCase)

    // ── Presentation layer ────────────────────────────────────────────────────

    factoryOf(::BookingsViewModel)

    // AppointmentDetailViewModel requires appointmentId — injected as a parameter
    factory { (appointmentId: String) ->
        AppointmentDetailViewModel(
            appointmentId = appointmentId,
            getAppointmentDetailUseCase = get(),
            cancelAppointmentUseCase = get(),
        )
    }
}

package com.daragly.daraglyhair.feature.shopbooking.di

import com.daragly.daraglyhair.feature.shopbooking.data.remote.ShopBookingRemoteDataSource
import com.daragly.daraglyhair.feature.shopbooking.data.repository.ShopBookingRepository
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.CreateAppointmentUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetAvailableSlotsUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetBarberProfileUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetShopBarbersUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.interactors.GetShopServicesUseCase
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingDraftStateHolder
import com.daragly.daraglyhair.feature.shopbooking.domain.repository.IShopBookingRepository
import com.daragly.daraglyhair.feature.shopbooking.ui.BarberDetailsViewModel
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingDateTimeViewModel
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingReviewViewModel
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingServicesViewModel
import com.daragly.daraglyhair.feature.shopbooking.ui.BookingSuccessViewModel
import com.daragly.daraglyhair.feature.shopbooking.ui.ShopDetailsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val shopBookingModule = module {

    // ── Data layer ────────────────────────────────────────────────────────────

    singleOf(::ShopBookingRemoteDataSource)
    singleOf(::ShopBookingRepository) bind IShopBookingRepository::class

    // Survives across the multi-screen booking flow; reset when a new flow starts.
    singleOf(::BookingDraftStateHolder)

    // ── Domain layer ──────────────────────────────────────────────────────────

    factoryOf(::GetShopBarbersUseCase)
    factoryOf(::GetBarberProfileUseCase)
    factoryOf(::GetShopServicesUseCase)
    factoryOf(::GetAvailableSlotsUseCase)
    factoryOf(::CreateAppointmentUseCase)

    // ── Presentation layer (parameterized factories, consumed via parametersOf) ──

    factory { (shopId: String) ->
        ShopDetailsViewModel(
            shopId = shopId,
            homeRepository = get(),
            getShopBarbersUseCase = get(),
            getShopServicesUseCase = get(),
            getBarberProfileUseCase = get(),
            draftHolder = get(),
        )
    }

    factory { (shopId: String, barberId: String) ->
        BarberDetailsViewModel(
            shopId = shopId,
            barberId = barberId,
            homeRepository = get(),
            getBarberProfileUseCase = get(),
            getShopBarbersUseCase = get(),
            draftHolder = get(),
        )
    }

    factory { (shopId: String) ->
        BookingServicesViewModel(
            shopId = shopId,
            getShopServicesUseCase = get(),
            draftHolder = get(),
        )
    }

    factory { (shopId: String) ->
        BookingDateTimeViewModel(
            shopId = shopId,
            getAvailableSlotsUseCase = get(),
            draftHolder = get(),
        )
    }

    factory { (shopId: String) ->
        BookingReviewViewModel(
            shopId = shopId,
            createAppointmentUseCase = get(),
            draftHolder = get(),
        )
    }

    factory { (shopId: String, appointmentId: String) ->
        BookingSuccessViewModel(
            shopId = shopId,
            appointmentId = appointmentId,
            getAppointmentDetailUseCase = get(),
        )
    }
}

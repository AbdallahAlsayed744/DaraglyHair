package com.daragly.daraglyhair.feature.shopbooking.domain.repository

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BarberProfile
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingSubmission
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopBarber
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopService
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotOffer
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotQuery

interface IShopBookingRepository {

    /** Shop-barber relationships for a shop (all statuses — callers filter by bookability). */
    suspend fun getShopBarbers(shopId: String): Resource<List<ShopBarber>>

    /** Barber profile by barber (profile) id. */
    suspend fun getBarberProfile(barberId: String): Resource<BarberProfile>

    /** Shop services with catalog names resolved (best-effort). */
    suspend fun getShopServices(shopId: String): Resource<List<ShopService>>

    /** Real available slots for the given query — never generated client-side. */
    suspend fun getAvailableSlots(query: SlotQuery): Resource<SlotOffer>

    /** Creates the appointment; returns the backend-confirmed appointment id. */
    suspend fun createAppointment(submission: BookingSubmission): Resource<String>
}

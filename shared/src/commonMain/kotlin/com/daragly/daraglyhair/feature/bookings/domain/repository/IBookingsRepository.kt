package com.daragly.daraglyhair.feature.bookings.domain.repository

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingTabFilter

interface IBookingsRepository {

    /**
     * Searches appointments matching the given [tab] filter.
     * Enriches each appointment with the shop name (best-effort).
     */
    suspend fun getAppointments(
        tab: BookingTabFilter,
        pageNumber: Int = 1,
        pageSize: Int = 20,
    ): Resource<List<BookingAppointment>>

    /**
     * Fetches full appointment details by [id].
     * Enriches with shop name (best-effort).
     */
    suspend fun getAppointmentById(id: String): Resource<BookingAppointment>

    /**
     * Requests cancellation of appointment [id].
     * Returns success/failure — does not guarantee refund.
     */
    suspend fun cancelAppointment(id: String): Resource<Unit>
}

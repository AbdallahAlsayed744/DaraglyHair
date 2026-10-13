package com.daragly.daraglyhair.feature.bookings.data.remote

import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.feature.bookings.data.dto.AppointmentDetailDto
import com.daragly.daraglyhair.feature.home.data.dto.AppointmentDto
import com.daragly.daraglyhair.feature.home.data.dto.BarbershopDto
import com.daragly.daraglyhair.feature.home.data.dto.PaginatedResponse
import com.daragly.daraglyhair.feature.home.data.request.AppointmentSearchRequest
import kotlinx.serialization.builtins.serializer

class BookingsRemoteDataSource(private val remote: IRemoteDataSourceProvider) {

    /** POST /api/v1/appointments/search */
    suspend fun searchAppointments(
        request: AppointmentSearchRequest,
    ): PaginatedResponse<AppointmentDto> = remote.post(
        endpoint = APPOINTMENTS_SEARCH_ENDPOINT,
        requestBody = request,
        serializer = PaginatedResponse.serializer(AppointmentDto.serializer()),
    )

    /** GET /api/v1/appointments/{id} */
    suspend fun getAppointmentById(id: String): AppointmentDetailDto = remote.get(
        endpoint = "$APPOINTMENTS_ENDPOINT/$id",
        serializer = AppointmentDetailDto.serializer(),
    )

    /**
     * POST /api/v1/appointments/{id}/cancel
     * Returns Boolean — success is indicated by no exception being thrown.
     */
    suspend fun cancelAppointment(id: String): Boolean = remote.post(
        endpoint = "$APPOINTMENTS_ENDPOINT/$id/cancel",
        requestBody = null,
        serializer = Boolean.serializer(),
    )

    /** GET /api/v1/barbershops/{id} — used for name enrichment */
    suspend fun getBarbershopById(id: String): BarbershopDto = remote.get(
        endpoint = "$BARBERSHOPS_ENDPOINT/$id",
        serializer = BarbershopDto.serializer(),
    )

    companion object {
        const val APPOINTMENTS_ENDPOINT = "api/v1/appointments"
        const val APPOINTMENTS_SEARCH_ENDPOINT = "api/v1/appointments/search"
        const val BARBERSHOPS_ENDPOINT = "api/v1/barbershops"
    }
}

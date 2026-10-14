package com.daragly.daraglyhair.feature.shopbooking.data.remote

import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.feature.home.data.dto.PaginatedResponse
import com.daragly.daraglyhair.feature.shopbooking.data.dto.AvailableSlotsResponseDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.BarberDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.ServiceCatalogDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.ShopBarberDto
import com.daragly.daraglyhair.feature.shopbooking.data.dto.ShopServiceDto
import com.daragly.daraglyhair.feature.shopbooking.data.request.AvailableSlotsApiRequest
import com.daragly.daraglyhair.feature.shopbooking.data.request.CreateAppointmentApiRequest
import com.daragly.daraglyhair.feature.shopbooking.data.request.ServiceSearchApiRequest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class ShopBookingRemoteDataSource(private val remote: IRemoteDataSourceProvider) {

    /** GET /api/v1/barbers/shop/{shopId} — shop-barber relationships. `id` = shopBarberId. */
    suspend fun getShopBarbers(shopId: String): List<ShopBarberDto> =
        remote.get(
            endpoint = "$BARBERS_ENDPOINT/shop/$shopId",
            serializer = ListSerializer(ShopBarberDto.serializer()),
        )

    /** GET /api/v1/barbers/{id} — barber profile by barber (profile) id. */
    suspend fun getBarber(barberId: String): BarberDto =
        remote.get(
            endpoint = "$BARBERS_ENDPOINT/$barberId",
            serializer = BarberDto.serializer(),
        )

    /** GET /api/v1/services/shop/{shopId} — shop-service relationships. `id` = shopServiceId. */
    suspend fun getShopServices(shopId: String): List<ShopServiceDto> =
        remote.get(
            endpoint = "$SERVICES_ENDPOINT/shop/$shopId",
            serializer = ListSerializer(ShopServiceDto.serializer()),
        )

    /** POST /api/v1/services/search — paginated service catalog (for name resolution). */
    suspend fun searchServices(request: ServiceSearchApiRequest): PaginatedResponse<ServiceCatalogDto> =
        remote.post(
            endpoint = "$SERVICES_ENDPOINT/search",
            requestBody = request,
            serializer = PaginatedResponse.serializer(ServiceCatalogDto.serializer()),
        )

    /** POST /api/v1/appointments/available-slots */
    suspend fun getAvailableSlots(request: AvailableSlotsApiRequest): AvailableSlotsResponseDto =
        remote.post(
            endpoint = "$APPOINTMENTS_ENDPOINT/available-slots",
            requestBody = request,
            serializer = AvailableSlotsResponseDto.serializer(),
        )

    /** POST /api/v1/appointments — returns the created appointment id (raw JSON string). */
    suspend fun createAppointment(request: CreateAppointmentApiRequest): String =
        remote.post(
            endpoint = APPOINTMENTS_ENDPOINT,
            requestBody = request,
            serializer = String.serializer(),
        )

    companion object {
        const val BARBERS_ENDPOINT = "api/v1/barbers"
        const val SERVICES_ENDPOINT = "api/v1/services"
        const val APPOINTMENTS_ENDPOINT = "api/v1/appointments"
    }
}

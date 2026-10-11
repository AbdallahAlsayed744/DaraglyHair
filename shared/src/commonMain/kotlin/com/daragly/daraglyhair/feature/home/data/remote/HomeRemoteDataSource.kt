package com.daragly.daraglyhair.feature.home.data.remote

import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.feature.home.data.dto.AppointmentDto
import com.daragly.daraglyhair.feature.home.data.dto.BarbershopDto
import com.daragly.daraglyhair.feature.home.data.dto.FavoriteDto
import com.daragly.daraglyhair.feature.home.data.dto.PaginatedResponse
import com.daragly.daraglyhair.feature.home.data.dto.ServiceCategoryDto
import com.daragly.daraglyhair.feature.home.data.dto.UserProfileDto
import com.daragly.daraglyhair.feature.home.data.request.AppointmentSearchRequest
import com.daragly.daraglyhair.feature.home.data.request.BarbershopSearchRequest
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

class HomeRemoteDataSource(private val remote: IRemoteDataSourceProvider) {

    // GET /api/personal/profile → returns user profile
    suspend fun getUserProfile(): UserProfileDto = remote.get(
        endpoint = USER_PROFILE_ENDPOINT,
        serializer = UserProfileDto.serializer(),
    )

    // POST /api/v1/barbershops/search → returns paginated barbershops
    suspend fun searchBarbershops(request: BarbershopSearchRequest): PaginatedResponse<BarbershopDto> =
        remote.post(
            endpoint = BARBERSHOP_SEARCH_ENDPOINT,
            requestBody = request,
            serializer = PaginatedResponse.serializer(BarbershopDto.serializer()),
        )

    // GET /api/v1/barbershops/{id} → returns barbershop details
    suspend fun getBarbershopById(id: String): BarbershopDto = remote.get(
        endpoint = "$BARBERSHOP_ENDPOINT/$id",
        serializer = BarbershopDto.serializer(),
    )

    // GET /api/v1/services/categories → returns list of categories
    suspend fun getServiceCategories(): List<ServiceCategoryDto> = remote.get(
        endpoint = SERVICE_CATEGORIES_ENDPOINT,
        serializer = ListSerializer(ServiceCategoryDto.serializer()),
    )

    // GET /api/v1/favorites → returns list of user's favorites
    suspend fun getFavorites(): List<FavoriteDto> = remote.get(
        endpoint = FAVORITES_ENDPOINT,
        serializer = ListSerializer(FavoriteDto.serializer()),
    )

    // POST /api/v1/favorites/{shopId}/toggle → toggles favorite status
    suspend fun toggleFavorite(shopId: String): Boolean = remote.post(
        endpoint = "$FAVORITES_ENDPOINT/$shopId/toggle",
        requestBody = null,
        serializer = Boolean.serializer(),
    )

    // POST /api/v1/appointments/search → returns paginated appointments
    suspend fun searchAppointments(request: AppointmentSearchRequest): PaginatedResponse<AppointmentDto> =
        remote.post(
            endpoint = APPOINTMENTS_SEARCH_ENDPOINT,
            requestBody = request,
            serializer = PaginatedResponse.serializer(AppointmentDto.serializer()),
        )

    companion object {
        const val USER_PROFILE_ENDPOINT = "api/personal/profile"
        const val BARBERSHOP_SEARCH_ENDPOINT = "api/v1/barbershops/search"
        const val BARBERSHOP_ENDPOINT = "api/v1/barbershops"
        const val SERVICE_CATEGORIES_ENDPOINT = "api/v1/services/categories"
        const val FAVORITES_ENDPOINT = "api/v1/favorites"
        const val APPOINTMENTS_SEARCH_ENDPOINT = "api/v1/appointments/search"
    }
}

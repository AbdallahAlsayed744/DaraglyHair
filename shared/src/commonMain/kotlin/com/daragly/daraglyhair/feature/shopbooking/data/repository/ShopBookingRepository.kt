package com.daragly.daraglyhair.feature.shopbooking.data.repository

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.shopbooking.data.dto.ServiceCatalogDto
import com.daragly.daraglyhair.feature.shopbooking.data.mapper.toDomain
import com.daragly.daraglyhair.feature.shopbooking.data.remote.ShopBookingRemoteDataSource
import com.daragly.daraglyhair.feature.shopbooking.data.request.AvailableSlotsApiRequest
import com.daragly.daraglyhair.feature.shopbooking.data.request.CreateAppointmentApiRequest
import com.daragly.daraglyhair.feature.shopbooking.data.request.ServiceSearchApiRequest
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BarberProfile
import com.daragly.daraglyhair.feature.shopbooking.domain.model.BookingSubmission
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopBarber
import com.daragly.daraglyhair.feature.shopbooking.domain.model.ShopService
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotOffer
import com.daragly.daraglyhair.feature.shopbooking.domain.model.SlotQuery
import com.daragly.daraglyhair.feature.shopbooking.domain.repository.IShopBookingRepository

class ShopBookingRepository(
    private val remoteDataSource: ShopBookingRemoteDataSource,
) : IShopBookingRepository {

    override suspend fun getShopBarbers(shopId: String): Resource<List<ShopBarber>> = try {
        Resource.Success(remoteDataSource.getShopBarbers(shopId).map { it.toDomain() })
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getBarberProfile(barberId: String): Resource<BarberProfile> = try {
        Resource.Success(remoteDataSource.getBarber(barberId).toDomain())
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getShopServices(shopId: String): Resource<List<ShopService>> = try {
        val dtos = remoteDataSource.getShopServices(shopId)
        val catalog = fetchCatalogNames()
        Resource.Success(dtos.map { it.toDomain(catalog) })
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getAvailableSlots(query: SlotQuery): Resource<SlotOffer> = try {
        val response = remoteDataSource.getAvailableSlots(
            AvailableSlotsApiRequest(
                shopId = query.shopId,
                shopBarberId = query.shopBarberId,
                date = query.date.toString(),
                durationMinutes = query.durationMinutes,
                userLatitude = query.userLatitude,
                userLongitude = query.userLongitude,
                transportationMode = query.transportationMode.apiValue,
                shopServiceIds = query.shopServiceIds,
            ),
        )
        Resource.Success(response.toDomain())
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun createAppointment(submission: BookingSubmission): Resource<String> = try {
        if (submission.shopServiceIds.isEmpty()) {
            return Resource.Failure(
                DaraglyHairException.Local.RequestValidation(message = "At least one service is required"),
            )
        }
        val appointmentId = remoteDataSource.createAppointment(
            CreateAppointmentApiRequest(
                shopId = submission.shopId,
                shopBarberId = submission.shopBarberId,
                startAtUtc = submission.startAtUtc.toString(),
                shopServiceIds = submission.shopServiceIds,
                notes = submission.notes?.takeIf { it.isNotBlank() },
                userLatitude = submission.userLatitude,
                userLongitude = submission.userLongitude,
                transportationMode = submission.transportationMode.apiValue,
            ),
        )
        if (appointmentId.isBlank()) {
            Resource.Failure(DaraglyHairException.UnKnown(message = "Empty appointment id"))
        } else {
            Resource.Success(appointmentId)
        }
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Fetches the service catalog (paged, capped) to resolve shop-service names.
     * Failures are non-fatal — names simply fall back to null.
     */
    private suspend fun fetchCatalogNames(): Map<String, ServiceCatalogDto> {
        val catalog = mutableMapOf<String, ServiceCatalogDto>()
        try {
            var page = 1
            while (page <= MAX_CATALOG_PAGES) {
                val response = remoteDataSource.searchServices(ServiceSearchApiRequest(pageNumber = page))
                response.getItemsList().forEach { catalog[it.id] = it }
                if (!response.hasNextPage) break
                page++
            }
        } catch (_: Exception) {
            // Name resolution is best-effort.
        }
        return catalog
    }

    companion object {
        private const val MAX_CATALOG_PAGES = 5
    }
}

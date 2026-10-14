package com.daragly.daraglyhair.feature.bookings.data.repository

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.bookings.data.dto.CancelAppointmentRequest
import com.daragly.daraglyhair.feature.bookings.data.mapper.toBookingDomain
import com.daragly.daraglyhair.feature.bookings.data.remote.BookingsRemoteDataSource
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingAppointment
import com.daragly.daraglyhair.feature.bookings.domain.model.BookingTabFilter
import com.daragly.daraglyhair.feature.bookings.domain.model.CancellationRequest
import com.daragly.daraglyhair.feature.bookings.domain.repository.IBookingsRepository
import com.daragly.daraglyhair.feature.home.data.request.AppointmentSearchRequest
import kotlinx.datetime.Clock

class BookingsRepository(
    private val remoteDataSource: BookingsRemoteDataSource,
) : IBookingsRepository {

    /**
     * Fetches appointments matching the tab.
     * - Upcoming: status in [1,2,7] — Pending, Confirmed, Arrived
     * - Completed: status = 3
     * - Cancelled: status in [4,5,6]
     * Enriches with shop names (best-effort, failures are non-fatal).
     */
    override suspend fun getAppointments(
        tab: BookingTabFilter,
        pageNumber: Int,
        pageSize: Int,
    ): Resource<List<BookingAppointment>> = try {
        // Build status-specific request
        val statusValues = when (tab) {
            BookingTabFilter.UPCOMING  -> listOf(1, 2, 7)   // Pending, Confirmed, Arrived
            BookingTabFilter.COMPLETED -> listOf(3)          // Completed
            BookingTabFilter.CANCELLED -> listOf(4, 5, 6)   // CancelledByCustomer, CancelledByShop, NoShow
        }

        // The API accepts a single status filter — fetch each and merge, or fetch all then filter
        // Since the API supports a single 'status' param, we fetch without status and filter client-side
        val request = AppointmentSearchRequest(
            pageNumber = pageNumber,
            pageSize = pageSize,
        )

        val response = remoteDataSource.searchAppointments(request)
        val allDtos = response.getItemsList()

        // Client-side filter by status group
        val filtered = allDtos.filter { dto ->
            dto.status in statusValues
        }

        // Sort: upcoming by soonest first, others by most recent first
        val sorted = when (tab) {
            BookingTabFilter.UPCOMING  -> filtered.sortedBy { it.startAtUtc }
            BookingTabFilter.COMPLETED,
            BookingTabFilter.CANCELLED -> filtered.sortedByDescending { it.startAtUtc }
        }

        // Enrich with shop names — collect unique shop IDs and fetch in parallel (best-effort)
        val shopNames = fetchShopNamesMap(sorted.map { it.shopId }.distinct())

        val appointments = sorted.map { dto ->
            dto.toBookingDomain(shopName = shopNames[dto.shopId])
        }

        Resource.Success(appointments)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getAppointmentById(id: String): Resource<BookingAppointment> = try {
        val dto = remoteDataSource.getAppointmentById(id)

        // Enrich with shop name
        val shopName = try {
            remoteDataSource.getBarbershopById(dto.shopId).name
        } catch (_: Exception) { null }

        Resource.Success(dto.toBookingDomain(shopName = shopName))
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun cancelAppointment(request: CancellationRequest): Resource<Unit> = try {
        remoteDataSource.cancelAppointment(
            CancelAppointmentRequest(
                id = request.appointmentId,
                reason = request.reason?.takeIf { it.isNotBlank() },
            ),
        ) // returned appointment id is not needed — success = no exception
        Resource.Success(Unit)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Fetches shop names for a list of shop IDs.
     * Failures are silently swallowed — the caller degrades gracefully.
     */
    private suspend fun fetchShopNamesMap(shopIds: List<String>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (shopId in shopIds) {
            try {
                val shop = remoteDataSource.getBarbershopById(shopId)
                map[shopId] = shop.name
            } catch (_: Exception) {
                // Non-fatal: shop name is optional
            }
        }
        return map
    }
}

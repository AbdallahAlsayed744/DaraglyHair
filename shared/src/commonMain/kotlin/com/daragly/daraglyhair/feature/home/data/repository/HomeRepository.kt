package com.daragly.daraglyhair.feature.home.data.repository

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.home.data.mapper.toDomain
import com.daragly.daraglyhair.feature.home.data.remote.HomeRemoteDataSource
import com.daragly.daraglyhair.feature.home.data.request.AppointmentSearchRequest
import com.daragly.daraglyhair.feature.home.data.request.BarbershopSearchRequest
import com.daragly.daraglyhair.feature.home.domain.model.Appointment
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.home.domain.model.ServiceCategory
import com.daragly.daraglyhair.feature.home.domain.model.UserProfile
import com.daragly.daraglyhair.feature.home.domain.model.isUpcoming
import com.daragly.daraglyhair.feature.home.domain.repository.IHomeRepository
import kotlinx.datetime.Clock

class HomeRepository(
    private val remoteDataSource: HomeRemoteDataSource,
) : IHomeRepository {

    override suspend fun getUserProfile(): Resource<UserProfile> = try {
        val profile = remoteDataSource.getUserProfile()
        Resource.Success(profile.toDomain())
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun searchBarbershops(
        pageNumber: Int,
        pageSize: Int,
        keyword: String?,
        latitude: Double?,
        longitude: Double?,
        maxDistanceKm: Double?,
        governorate: Int?,
        city: String?,
        district: String?,
        isVerified: Boolean?,
        isBookingEnabled: Boolean?,
    ): Resource<List<Barbershop>> = try {
        val request = BarbershopSearchRequest(
            pageNumber = pageNumber,
            pageSize = pageSize,
            keyword = keyword,
            latitude = latitude,
            longitude = longitude,
            maxDistanceKm = maxDistanceKm,
            governorate = governorate,
            city = city,
            district = district,
            isActive = true,
            isVerified = isVerified,
            isBookingEnabled = isBookingEnabled,
        )
        val response = remoteDataSource.searchBarbershops(request)
        
        // Get favorites to mark shops
        val favoriteIds = try {
            remoteDataSource.getFavorites().map { it.shopId }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
        
        val shops = response.getItemsList().map { dto ->
            dto.toDomain(isFavorite = favoriteIds.contains(dto.id))
        }
        
        Resource.Success(shops)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getServiceCategories(): Resource<List<ServiceCategory>> = try {
        val categories = remoteDataSource.getServiceCategories()
        val activeCategories = categories
            .filter { it.isActive }
            .sortedBy { it.sortOrder }
            .map { it.toDomain() }
        Resource.Success(activeCategories)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getFavoriteShopIds(): Resource<Set<String>> = try {
        val favorites = remoteDataSource.getFavorites()
        Resource.Success(favorites.map { it.shopId }.toSet())
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun toggleFavorite(shopId: String): Resource<Boolean> = try {
        val isNowFavorite = remoteDataSource.toggleFavorite(shopId)
        Resource.Success(isNowFavorite)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getUpcomingAppointment(): Resource<Appointment?> = try {
        val now = Clock.System.now()
        
        val request = AppointmentSearchRequest(
            pageNumber = 1,
            pageSize = 20, // Get more to find next upcoming after filtering
            fromUtc = now.toString(),
        )
        
        val response = remoteDataSource.searchAppointments(request)
        
        val upcomingAppointments = response.getItemsList()
            .map { it.toDomain() }
            .filter { it.status.isUpcoming() && it.startAt > now }
            .sortedBy { it.startAt }
        
        val nextAppointment = upcomingAppointments.firstOrNull()
        
        // Fetch shop name if appointment exists
        val appointmentWithShopName = nextAppointment?.let { appointment ->
            try {
                val shop = remoteDataSource.getBarbershopById(appointment.shopId)
                appointment.copy(shopName = shop.displayName)
            } catch (e: Exception) {
                // If shop fetch fails, return appointment without shop name
                appointment
            }
        }
        
        Resource.Success(appointmentWithShopName)
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }

    override suspend fun getBarbershopById(id: String): Resource<Barbershop> = try {
        val shop = remoteDataSource.getBarbershopById(id)
        
        // Check if it's in favorites
        val favoriteIds = try {
            remoteDataSource.getFavorites().map { it.shopId }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
        
        Resource.Success(shop.toDomain(isFavorite = favoriteIds.contains(id)))
    } catch (e: DaraglyHairException) {
        Resource.Failure(e)
    }
}

package com.daragly.daraglyhair.feature.home.domain.repository

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.feature.home.domain.model.Appointment
import com.daragly.daraglyhair.feature.home.domain.model.Barbershop
import com.daragly.daraglyhair.feature.home.domain.model.ServiceCategory
import com.daragly.daraglyhair.feature.home.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    
    /**
     * Get the current user's profile.
     */
    suspend fun getUserProfile(): Resource<UserProfile>
    
    /**
     * Search for barbershops with optional filters.
     * Passing [latitude]/[longitude] enables server-side geo search (default 25 km
     * radius) with distance-sorted results and `distanceKm` on each shop.
     */
    suspend fun searchBarbershops(
        pageNumber: Int = 1,
        pageSize: Int = 10,
        keyword: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        maxDistanceKm: Double? = null,
        governorate: Int? = null,
        city: String? = null,
        district: String? = null,
        isVerified: Boolean? = null,
        isBookingEnabled: Boolean? = null,
    ): Resource<List<Barbershop>>
    
    /**
     * Get all service categories.
     */
    suspend fun getServiceCategories(): Resource<List<ServiceCategory>>
    
    /**
     * Get the user's favorite barbershop IDs.
     */
    suspend fun getFavoriteShopIds(): Resource<Set<String>>
    
    /**
     * Toggle a shop's favorite status.
     * Returns true if added, false if removed.
     */
    suspend fun toggleFavorite(shopId: String): Resource<Boolean>
    
    /**
     * Get the next upcoming appointment for the user.
     */
    suspend fun getUpcomingAppointment(): Resource<Appointment?>
    
    /**
     * Get barbershop details by ID.
     */
    suspend fun getBarbershopById(id: String): Resource<Barbershop>
}

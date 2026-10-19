package com.daragly.daraglyhair.common.domain.location

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** WGS-84 coordinates of the user's current position. */
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
)

/**
 * Device location source. Returns null when the position is unavailable
 * (permission denied, location services off, or no fix within the timeout).
 */
interface LocationProvider {
    suspend fun currentLocation(): LocationCoordinates?
}

/**
 * Koin single caching the last device fix. The first caller pays the GPS cost;
 * later callers reuse the result. A failed (null) fix is retried on the next
 * call — e.g. right after the user grants the location permission.
 */
class UserLocationHolder(private val provider: LocationProvider) {

    private val mutex = Mutex()
    private var cached: LocationCoordinates? = null

    suspend fun currentLocation(): LocationCoordinates? = mutex.withLock {
        cached ?: provider.currentLocation().also { cached = it }
    }
}

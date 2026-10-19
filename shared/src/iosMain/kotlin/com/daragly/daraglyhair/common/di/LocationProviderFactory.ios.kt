package com.daragly.daraglyhair.common.di

import com.daragly.daraglyhair.common.domain.location.LocationCoordinates
import com.daragly.daraglyhair.common.domain.location.LocationProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import platform.CoreLocation.CLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.CLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.CLAuthorizationStatusNotDetermined
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.Foundation.NSObject

actual fun createLocationProvider(): LocationProvider = IosLocationProvider()

/**
 * CoreLocation source. One [CLLocationManager] instance is kept alive for the
 * process — its delegate is weak, so the one-shot delegate is also retained
 * here until the fix (or failure) arrives.
 */
private class IosLocationProvider : LocationProvider {

    private val manager = CLLocationManager()
    private var retainedDelegate: OneFixDelegate? = null

    override suspend fun currentLocation(): LocationCoordinates? {
        when (manager.authorizationStatus) {
            CLAuthorizationStatusNotDetermined -> {
                // Prompt now; the fix is picked up on a later call once access is granted.
                manager.requestWhenInUseAuthorization()
                return null
            }
            CLAuthorizationStatusAuthorizedWhenInUse, CLAuthorizationStatusAuthorizedAlways -> Unit
            else -> return null
        }
        if (!CLLocationManager.locationServicesEnabled()) return null
        return withTimeoutOrNull(FIX_TIMEOUT_MS) { requestOneFix() }
    }

    private suspend fun requestOneFix(): LocationCoordinates? = suspendCancellableCoroutine { cont ->
        val delegate = OneFixDelegate(
            onResult = { coordinates ->
                retainedDelegate = null
                if (cont.isActive) cont.resume(coordinates)
            },
            onError = {
                retainedDelegate = null
                if (cont.isActive) cont.resume(null)
            },
        )
        retainedDelegate = delegate
        manager.delegate = delegate
        manager.requestLocation()
        cont.invokeOnCancellation {
            retainedDelegate = null
            manager.delegate = null
        }
    }

    private companion object {
        private const val FIX_TIMEOUT_MS = 10_000L
    }
}

private class OneFixDelegate(
    private val onResult: (LocationCoordinates?) -> Unit,
    private val onError: () -> Unit,
) : NSObject(), CLLocationManagerDelegateProtocol {

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation
        onResult(location?.toCoordinates())
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onError()
    }

    private fun CLLocation.toCoordinates() =
        LocationCoordinates(latitude = coordinate.latitude, longitude = coordinate.longitude)
}

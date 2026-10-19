package com.daragly.daraglyhair.common.di

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import android.os.Looper
import com.daragly.daraglyhair.common.domain.location.LocationCoordinates
import com.daragly.daraglyhair.common.domain.location.LocationProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.mp.KoinPlatform.getKoin
import kotlin.coroutines.resume

actual fun createLocationProvider(): LocationProvider =
    AndroidLocationProvider(getKoin().get())

/**
 * Framework [LocationManager] source — no Google Play services dependency.
 * A last-known fix fresher than [LAST_KNOWN_MAX_AGE_MS] short-circuits the request;
 * otherwise a fresh single fix is requested (network provider first, then GPS).
 */
private class AndroidLocationProvider(private val context: Context) : LocationProvider {

    override suspend fun currentLocation(): LocationCoordinates? {
        if (!hasLocationPermission()) return null
        recentLastKnownLocation()?.let { return it.toCoordinates() }
        return withTimeoutOrNull(FIX_TIMEOUT_MS) { fetchFreshLocation() }
    }

    private fun hasLocationPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun locationManager(): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /** Best last-known location across providers, accepted only when recent. */
    @SuppressLint("MissingPermission")
    private fun recentLastKnownLocation(): Location? {
        val manager = locationManager()
        val minAge = System.currentTimeMillis() - LAST_KNOWN_MAX_AGE_MS
        return enabledProviders().mapNotNull { provider ->
            runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
        }.filter { it.time >= minAge }
            .maxByOrNull { it.time }
    }

    /** Single-fix attempts, fastest provider first; the caller bounds the total time. */
    @SuppressLint("MissingPermission")
    private suspend fun fetchFreshLocation(): LocationCoordinates? {
        val manager = locationManager()
        for (provider in enabledProviders()) {
            requestSingleFix(manager, provider)?.let { return it }
        }
        return null
    }

    private fun enabledProviders(): List<String> {
        val manager = locationManager()
        return listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
            .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
    }

    private suspend fun requestSingleFix(
        manager: LocationManager,
        provider: String,
    ): LocationCoordinates? = suspendCancellableCoroutine { cont ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val signal = CancellationSignal()
            manager.getCurrentLocation(provider, signal, context.mainExecutor) { location ->
                if (cont.isActive) cont.resume(location?.toCoordinates())
            }
            cont.invokeOnCancellation { signal.cancel() }
        } else {
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (cont.isActive) cont.resume(location.toCoordinates())
                }
            }
            @Suppress("DEPRECATION")
            manager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            cont.invokeOnCancellation { manager.removeUpdates(listener) }
        }
    }

    private fun Location.toCoordinates() = LocationCoordinates(latitude = latitude, longitude = longitude)

    private companion object {
        private const val FIX_TIMEOUT_MS = 10_000L
        private const val LAST_KNOWN_MAX_AGE_MS = 2 * 60_000L
    }
}

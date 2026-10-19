package com.daragly.daraglyhair.common.di

import com.daragly.daraglyhair.common.domain.location.LocationProvider

/**
 * Platform-provided location source.
 * Android → [android.location.LocationManager]
 * iOS     → CoreLocation's CLLocationManager
 */
expect fun createLocationProvider(): LocationProvider

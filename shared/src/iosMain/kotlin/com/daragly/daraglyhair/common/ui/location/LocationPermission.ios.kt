package com.daragly.daraglyhair.common.ui.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.CoreLocation.CLLocationManager

// Kept for the process lifetime; a deallocated manager can drop the authorization prompt.
private val authorizationManager = CLLocationManager()

@Composable
actual fun EnsureLocationPermission(onResult: (granted: Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        authorizationManager.requestWhenInUseAuthorization()
        // The provider re-checks authorization on every call and caches once a fix arrives,
        // so proceed now — the permission-gated reload will pick up real coordinates next time.
        onResult(true)
    }
}

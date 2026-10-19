package com.daragly.daraglyhair.common.ui.location

import androidx.compose.runtime.Composable

/**
 * Requests the device location permission once per screen entry and reports the outcome
 * to [onResult]. If permission is already granted, [onResult] fires immediately with true.
 *
 * Screens call this on first composition; when [onResult] reports a grant they re-run the
 * geo-aware load so the real user coordinates reach the barbershop-search and slot endpoints.
 * The actual prompt is platform-specific (Android system dialog / iOS CoreLocation).
 */
@Composable
expect fun EnsureLocationPermission(onResult: (granted: Boolean) -> Unit)

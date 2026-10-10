package com.daragly.daraglyhair

import androidx.compose.runtime.Composable

/**
 * Top-level entry point called from every platform (Android / iOS).
 * Delegates to [HallaakApp] which owns the NavHost, Koin injections,
 * and the global event observers — exactly like DelivaryUserApp in Delivery-User.
 */
@Composable
fun App() {
    HallaakApp()
}

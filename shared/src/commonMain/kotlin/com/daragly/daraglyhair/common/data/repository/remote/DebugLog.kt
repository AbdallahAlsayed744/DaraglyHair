package com.daragly.daraglyhair.common.data.repository.remote

/**
 * Temporary network/auth tracing. Presence flags only — never token values.
 * Flip [enabled] off (or delete the call sites) once the refresh flow is verified on device.
 */
object DebugLog {

    var enabled: Boolean = true

    inline fun d(message: () -> String) {
        if (enabled) println(message())
    }
}

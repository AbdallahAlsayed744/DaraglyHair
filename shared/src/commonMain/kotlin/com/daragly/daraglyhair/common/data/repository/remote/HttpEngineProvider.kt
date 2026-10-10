package com.daragly.daraglyhair.common.data.repository.remote

import io.ktor.client.engine.HttpClientEngineFactory

/**
 * Platform-provided Ktor engine factory.
 * Android → [io.ktor.client.engine.android.Android]
 * iOS     → [io.ktor.client.engine.darwin.Darwin]
 */
expect fun httpClientEngineFactory(): HttpClientEngineFactory<*>

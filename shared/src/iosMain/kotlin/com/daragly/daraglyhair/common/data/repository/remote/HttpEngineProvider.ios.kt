package com.daragly.daraglyhair.common.data.repository.remote

import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.HttpClientEngineFactory

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Darwin

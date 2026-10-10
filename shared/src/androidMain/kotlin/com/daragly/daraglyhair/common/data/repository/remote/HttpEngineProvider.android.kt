package com.daragly.daraglyhair.common.data.repository.remote

import io.ktor.client.engine.android.Android
import io.ktor.client.engine.HttpClientEngineFactory

actual fun httpClientEngineFactory(): HttpClientEngineFactory<*> = Android

package com.daragly.daraglyhair.common.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Platform-provided DataStore instance.
 * Android → created from Context using [preferencesDataStoreFile].
 * iOS     → created from the app's Documents directory.
 */
expect fun createDataStore(): DataStore<Preferences>

internal const val DATASTORE_FILE_NAME = "daragly_hair_prefs.preferences_pb"

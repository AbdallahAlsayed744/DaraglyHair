package com.daragly.daraglyhair.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import org.koin.mp.KoinPlatform.getKoin

actual fun createDataStore(): DataStore<Preferences> {
    val context: Context = getKoin().get()
    return PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile(DATASTORE_FILE_NAME)
    }
}

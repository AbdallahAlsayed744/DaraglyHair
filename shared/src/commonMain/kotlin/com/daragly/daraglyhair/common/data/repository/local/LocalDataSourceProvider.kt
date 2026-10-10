package com.daragly.daraglyhair.common.data.repository.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceEnum
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import kotlinx.coroutines.flow.first
import kotlin.reflect.KClass

class LocalDataSourceProvider(
    private val dataStore: DataStore<Preferences>,
) : ILocalDataSourceProvider {

    @Suppress("UNCHECKED_CAST")
    private fun <Value> preferencesKey(
        key: ILocalDataSourceEnum,
        type: KClass<*>,
    ): Preferences.Key<Value> = when (type) {
        Boolean::class -> booleanPreferencesKey(key.keyValue)
        Float::class -> floatPreferencesKey(key.keyValue)
        Int::class -> intPreferencesKey(key.keyValue)
        Long::class -> longPreferencesKey(key.keyValue)
        String::class -> stringPreferencesKey(key.keyValue)
        Set::class -> stringSetPreferencesKey(key.keyValue)
        else -> throw DaraglyHairException.Local.IOOperation(
            message = "Unsupported DataStore type: ${type.simpleName}"
        )
    } as Preferences.Key<Value>

    override suspend fun <Value> save(key: ILocalDataSourceEnum, value: Value, type: KClass<*>) {
        val prefKey = preferencesKey<Value>(key, type)
        dataStore.edit { prefs -> prefs[prefKey] = value }
    }

    override suspend fun <Value> read(
        key: ILocalDataSourceEnum,
        defaultValue: Value,
        type: KClass<*>,
    ): Value {
        val prefKey = preferencesKey<Value>(key, type)
        val prefs = dataStore.data.first()
        return prefs[prefKey] ?: defaultValue
    }

    override suspend fun <Value> delete(key: ILocalDataSourceEnum, type: KClass<*>) {
        val prefKey = preferencesKey<Value>(key, type)
        dataStore.edit { prefs -> prefs.remove(prefKey) }
    }
}

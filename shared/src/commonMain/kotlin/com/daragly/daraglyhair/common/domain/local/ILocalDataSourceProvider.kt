package com.daragly.daraglyhair.common.domain.local

interface ILocalDataSourceProvider {
    suspend fun <Value> save(key: ILocalDataSourceEnum, value: Value, type: kotlin.reflect.KClass<*>)
    suspend fun <Value> read(key: ILocalDataSourceEnum, defaultValue: Value, type: kotlin.reflect.KClass<*>): Value
    suspend fun <Value> delete(key: ILocalDataSourceEnum, type: kotlin.reflect.KClass<*>)
}

package com.daragly.daraglyhair.feature.user.data.repository.local

import com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceEnum
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import com.daragly.daraglyhair.feature.user.domain.model.User
import com.daragly.daraglyhair.feature.user.domain.repository.local.IUserLocalDataSource
import kotlinx.serialization.json.Json

class UserLocalDataSource(
    private val localProvider: ILocalDataSourceProvider,
    private val json: Json,
) : IUserLocalDataSource {

    override suspend fun saveToken(token: String) {
        localProvider.save(
            key = LocalDataSourceEnum.ACCESS_TOKEN,
            value = token,
            type = String::class
        )
    }

    override suspend fun getToken(): String {
        return localProvider.read(
            key = LocalDataSourceEnum.ACCESS_TOKEN,
            defaultValue = "",
            type = String::class
        )
    }

    override suspend fun saveRefreshToken(refreshToken: String) {
        localProvider.save(
            key = LocalDataSourceEnum.REFRESH_TOKEN,
            value = refreshToken,
            type = String::class
        )
    }

    override suspend fun getRefreshToken(): String {
        return localProvider.read(
            key = LocalDataSourceEnum.REFRESH_TOKEN,
            defaultValue = "",
            type = String::class
        )
    }

    override suspend fun saveUser(user: User) {
        val userJson = json.encodeToString(User.serializer(), user)
        localProvider.save(
            key = LocalDataSourceEnum.USER,
            value = userJson,
            type = String::class
        )
    }

    override suspend fun getUser(): User {
        val userJson = localProvider.read(
            key = LocalDataSourceEnum.USER,
            defaultValue = "",
            type = String::class
        )
        return if (userJson.isEmpty()) {
            User()
        } else {
            json.decodeFromString(User.serializer(), userJson)
        }
    }

    override suspend fun saveIsAuthenticated(isAuthenticated: Boolean) {
        localProvider.save(
            key = LocalDataSourceEnum.IS_AUTHENTICATED,
            value = isAuthenticated,
            type = Boolean::class
        )
    }

    override suspend fun getIsAuthenticated(): Boolean {
        return localProvider.read(
            key = LocalDataSourceEnum.IS_AUTHENTICATED,
            defaultValue = false,
            type = Boolean::class
        )
    }

    override suspend fun deleteToken() {
        localProvider.delete<String>(
            key = LocalDataSourceEnum.ACCESS_TOKEN,
            type = String::class
        )
    }

    override suspend fun deleteRefreshToken() {
        localProvider.delete<String>(
            key = LocalDataSourceEnum.REFRESH_TOKEN,
            type = String::class
        )
    }

    override suspend fun deleteUser() {
        localProvider.delete<String>(
            key = LocalDataSourceEnum.USER,
            type = String::class
        )
    }

    override suspend fun deleteIsAuthenticated() {
        localProvider.delete<Boolean>(
            key = LocalDataSourceEnum.IS_AUTHENTICATED,
            type = Boolean::class
        )
    }
}

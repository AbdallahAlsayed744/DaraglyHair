package com.daragly.daraglyhair.feature.user.data.repository

import com.daragly.daraglyhair.feature.user.domain.model.User
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository
import com.daragly.daraglyhair.feature.user.domain.repository.local.IUserLocalDataSource

class UserRepository(
    private val local: IUserLocalDataSource,
) : IUserRepository {

    override suspend fun saveIsAuthenticated(isAuthenticated: Boolean) =
        local.saveIsAuthenticated(isAuthenticated)

    override suspend fun getIsAuthenticated(): Boolean =
        local.getIsAuthenticated()

    override suspend fun saveUser(user: User) =
        local.saveUser(user)

    override suspend fun getUser(): User =
        local.getUser()

    override suspend fun saveToken(token: String) =
        local.saveToken(token)

    override suspend fun getToken(): String =
        local.getToken()

    override suspend fun deleteToken() =
        local.deleteToken()

    override suspend fun deleteUser() =
        local.deleteUser()

    override suspend fun deleteIsAuthenticated() =
        local.deleteIsAuthenticated()
}

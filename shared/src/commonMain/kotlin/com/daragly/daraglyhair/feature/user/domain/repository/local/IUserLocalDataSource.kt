package com.daragly.daraglyhair.feature.user.domain.repository.local

import com.daragly.daraglyhair.feature.user.domain.model.User

interface IUserLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun getRefreshToken(): String
    suspend fun saveUser(user: User)
    suspend fun getUser(): User
    suspend fun saveIsAuthenticated(isAuthenticated: Boolean)
    suspend fun getIsAuthenticated(): Boolean
    suspend fun deleteToken()
    suspend fun deleteRefreshToken()
    suspend fun deleteUser()
    suspend fun deleteIsAuthenticated()
}

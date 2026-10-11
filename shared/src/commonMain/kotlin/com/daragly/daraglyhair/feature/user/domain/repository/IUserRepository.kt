package com.daragly.daraglyhair.feature.user.domain.repository

import com.daragly.daraglyhair.feature.user.domain.model.User

interface IUserRepository {
    suspend fun saveIsAuthenticated(isAuthenticated: Boolean)
    suspend fun getIsAuthenticated(): Boolean
    suspend fun saveUser(user: User)
    suspend fun getUser(): User
    suspend fun saveToken(token: String)
    suspend fun getToken(): String
    suspend fun deleteToken()
    suspend fun deleteUser()
    suspend fun deleteIsAuthenticated()
}

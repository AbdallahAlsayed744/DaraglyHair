package com.daragly.daraglyhair.feature.user.domain.repository

import com.daragly.daraglyhair.feature.user.domain.model.ProfileUpdate
import com.daragly.daraglyhair.feature.user.domain.model.User

interface IUserRepository {
    suspend fun saveIsAuthenticated(isAuthenticated: Boolean)
    suspend fun getIsAuthenticated(): Boolean
    suspend fun saveUser(user: User)
    suspend fun getUser(): User
    /** Pushes the profile to the backend; throws DaraglyHairException on failure. */
    suspend fun updateProfile(update: ProfileUpdate)
    suspend fun saveToken(token: String)
    suspend fun getToken(): String
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun getRefreshToken(): String
    suspend fun deleteToken()
    suspend fun deleteRefreshToken()
    suspend fun deleteUser()
    suspend fun deleteIsAuthenticated()
}

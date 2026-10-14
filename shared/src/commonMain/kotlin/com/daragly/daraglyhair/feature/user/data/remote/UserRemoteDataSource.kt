package com.daragly.daraglyhair.feature.user.data.remote

import com.daragly.daraglyhair.common.domain.remote.IRemoteDataSourceProvider
import com.daragly.daraglyhair.feature.user.data.dto.UpdateUserRequestDto
import kotlinx.serialization.builtins.serializer

class UserRemoteDataSource(private val remote: IRemoteDataSourceProvider) {

    /**
     * PUT /api/personal/profile — updates the currently authenticated user.
     * The API answers 200 with an empty body; success is signalled by no exception.
     */
    suspend fun updateProfile(request: UpdateUserRequestDto): String = remote.put(
        endpoint = PROFILE_ENDPOINT,
        requestBody = request,
        serializer = String.serializer(),
    )

    companion object {
        const val PROFILE_ENDPOINT = "api/personal/profile"
    }
}

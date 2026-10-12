package com.daragly.daraglyhair.feature.authentication.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.home.data.remote.HomeRemoteDataSource
import com.daragly.daraglyhair.feature.user.domain.model.User
import com.daragly.daraglyhair.feature.user.domain.usecase.SaveUserUseCase

/**
 * Fetches the authenticated user's profile from the API
 * and persists it in the local DataStore so that Settings,
 * Edit Profile, and any other screen can read it without
 * hitting the network again.
 *
 * Called once right after a successful login.
 */
class FetchAndSaveUserProfileUseCase(
    private val homeRemoteDataSource: HomeRemoteDataSource,
    private val saveUserUseCase: SaveUserUseCase,
) : BaseUseCase<Resource<Unit>, Unit>() {

    override suspend fun invoke(body: Unit): Resource<Unit> = nonFlowExecute {
        val dto = homeRemoteDataSource.getUserProfile()

        val user = User(
            id    = dto.id,
            name  = listOfNotNull(dto.firstName, dto.lastName)
                        .filter { it.isNotBlank() }
                        .joinToString(" ")
                        .trim(),
            email = dto.email.orEmpty(),
            phone = dto.phoneNumber.orEmpty(),
            image = dto.imageUrl,   // persist the profile image URL from the API
        )

        saveUserUseCase.invoke(user)
    }
}

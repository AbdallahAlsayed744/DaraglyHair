package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.model.ProfileUpdate
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

class UpdateUserProfileUseCase(
    private val repository: IUserRepository,
) : BaseUseCase<Resource<Unit>, ProfileUpdate>() {

    override suspend fun invoke(body: ProfileUpdate): Resource<Unit> = nonFlowExecute {
        repository.updateProfile(body)
    }
}

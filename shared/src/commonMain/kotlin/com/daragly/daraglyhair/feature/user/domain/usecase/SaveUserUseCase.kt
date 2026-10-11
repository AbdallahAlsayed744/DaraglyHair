package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.model.User
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

class SaveUserUseCase(
    private val repository: IUserRepository,
) : BaseUseCase<Unit, User>() {

    override suspend fun invoke(body: User) {
        repository.saveUser(body)
    }
}

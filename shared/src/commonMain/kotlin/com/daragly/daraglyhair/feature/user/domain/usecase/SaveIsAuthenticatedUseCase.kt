package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

class SaveIsAuthenticatedUseCase(
    private val repository: IUserRepository,
) : BaseUseCase<Unit, Boolean>() {

    override suspend fun invoke(body: Boolean) {
        repository.saveIsAuthenticated(body)
    }
}

package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

/**
 * Clears all session data: token, user, auth flag.
 * Must be called on explicit user sign-out only.
 */
class LogoutUseCase(
    private val repository: IUserRepository,
) : BaseUseCase<Resource<Unit>, Unit>() {

    override suspend fun invoke(body: Unit): Resource<Unit> = nonFlowExecute {
        repository.deleteToken()
        repository.deleteUser()
        repository.deleteIsAuthenticated()
    }
}

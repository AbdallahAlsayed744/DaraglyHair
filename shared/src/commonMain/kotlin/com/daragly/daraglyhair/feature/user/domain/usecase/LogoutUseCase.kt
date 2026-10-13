package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.data.repository.remote.SessionManager
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase

/**
 * Clears all session data: tokens, user, auth flag, and the client's cached bearer token.
 * Must be called on explicit user sign-out only.
 */
class LogoutUseCase(
    private val sessionManager: SessionManager,
) : BaseUseCase<Resource<Unit>, Unit>() {

    override suspend fun invoke(body: Unit): Resource<Unit> = nonFlowExecute {
        sessionManager.clearSession()
    }
}

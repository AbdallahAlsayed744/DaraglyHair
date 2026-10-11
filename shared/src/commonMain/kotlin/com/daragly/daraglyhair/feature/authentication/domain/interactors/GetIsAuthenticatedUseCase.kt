package com.daragly.daraglyhair.feature.authentication.domain.interactors

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

class GetIsAuthenticatedUseCase(
    private val userRepository: IUserRepository,
) : BaseUseCase<Resource<Boolean>, Unit>() {
    override suspend fun invoke(body: Unit): Resource<Boolean> = nonFlowExecute {
        userRepository.getIsAuthenticated()
    }
}

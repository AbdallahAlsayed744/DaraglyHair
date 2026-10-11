package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

class GetTokenUseCase(
    private val repository: IUserRepository,
) : BaseUseCase<Resource<String>, Unit>() {

    override suspend fun invoke(body: Unit): Resource<String> = nonFlowExecute {
        repository.getToken()
    }
}

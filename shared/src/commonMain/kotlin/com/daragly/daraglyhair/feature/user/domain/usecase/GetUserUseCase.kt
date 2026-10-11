package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.model.User
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

class GetUserUseCase(
    private val repository: IUserRepository,
) : BaseUseCase<Resource<User>, Unit>() {

    override suspend fun invoke(body: Unit): Resource<User> = nonFlowExecute {
        repository.getUser()
    }
}

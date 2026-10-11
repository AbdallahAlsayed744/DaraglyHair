package com.daragly.daraglyhair.feature.user.domain.usecase

import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository

class SaveTokenUseCase(
    private val repository: IUserRepository,
) : BaseUseCase<Unit, String>() {

    override suspend fun invoke(body: String) {
        repository.saveToken(body)
    }
}

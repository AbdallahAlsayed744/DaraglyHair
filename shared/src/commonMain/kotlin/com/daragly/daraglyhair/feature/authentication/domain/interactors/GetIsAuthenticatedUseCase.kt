package com.daragly.daraglyhair.feature.authentication.domain.interactors

import com.daragly.daraglyhair.common.data.repository.local.LocalDataSourceEnum
import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.local.ILocalDataSourceProvider
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase

class GetIsAuthenticatedUseCase(
    private val local: ILocalDataSourceProvider,
) : BaseUseCase<Resource<Boolean>, Unit>() {
    override suspend fun invoke(body: Unit): Resource<Boolean> = nonFlowExecute {
        local.read(LocalDataSourceEnum.IS_AUTHENTICATED, false, Boolean::class)
    }
}

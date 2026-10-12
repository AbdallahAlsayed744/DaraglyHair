package com.daragly.daraglyhair.feature.settings.domain.usecase

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.settings.domain.repository.ISettingsRepository

class GetLanguageUseCase(
    private val repository: ISettingsRepository,
) : BaseUseCase<Resource<String>, Unit>() {
    override suspend fun invoke(body: Unit): Resource<String> =
        nonFlowExecute { repository.getLanguage() }
}

package com.daragly.daraglyhair.feature.settings.domain.usecase

import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.settings.domain.repository.ISettingsRepository

class SaveLanguageUseCase(
    private val repository: ISettingsRepository,
) : BaseUseCase<Unit, String>() {
    override suspend fun invoke(body: String) = repository.saveLanguage(body)
}

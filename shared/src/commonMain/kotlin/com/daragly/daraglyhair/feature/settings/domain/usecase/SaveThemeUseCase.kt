package com.daragly.daraglyhair.feature.settings.domain.usecase

import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme
import com.daragly.daraglyhair.feature.settings.domain.repository.ISettingsRepository

class SaveThemeUseCase(
    private val repository: ISettingsRepository,
) : BaseUseCase<Unit, AppTheme>() {
    override suspend fun invoke(body: AppTheme) = repository.saveTheme(body)
}

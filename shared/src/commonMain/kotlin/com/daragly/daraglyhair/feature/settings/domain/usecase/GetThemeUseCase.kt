package com.daragly.daraglyhair.feature.settings.domain.usecase

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.settings.domain.model.AppTheme
import com.daragly.daraglyhair.feature.settings.domain.repository.ISettingsRepository

class GetThemeUseCase(
    private val repository: ISettingsRepository,
) : BaseUseCase<Resource<AppTheme>, Unit>() {
    override suspend fun invoke(body: Unit): Resource<AppTheme> =
        nonFlowExecute { repository.getTheme() }
}

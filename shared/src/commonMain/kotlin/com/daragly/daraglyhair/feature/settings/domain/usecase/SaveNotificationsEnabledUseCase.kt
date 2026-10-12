package com.daragly.daraglyhair.feature.settings.domain.usecase

import com.daragly.daraglyhair.common.domain.usecase.BaseUseCase
import com.daragly.daraglyhair.feature.settings.domain.repository.ISettingsRepository

class SaveNotificationsEnabledUseCase(
    private val repository: ISettingsRepository,
) : BaseUseCase<Unit, Boolean>() {
    override suspend fun invoke(body: Boolean) = repository.saveNotificationsEnabled(body)
}

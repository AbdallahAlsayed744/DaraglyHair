package com.daragly.daraglyhair.feature.settings.di

import com.daragly.daraglyhair.feature.settings.data.repository.SettingsRepository
import com.daragly.daraglyhair.feature.settings.domain.repository.ISettingsRepository
import com.daragly.daraglyhair.feature.settings.domain.usecase.GetLanguageUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.GetNotificationsEnabledUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.GetThemeUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.SaveLanguageUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.SaveNotificationsEnabledUseCase
import com.daragly.daraglyhair.feature.settings.domain.usecase.SaveThemeUseCase
import com.daragly.daraglyhair.feature.settings.ui.SettingsViewModel
import com.daragly.daraglyhair.feature.profile.ui.EditProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    // Repository
    singleOf(::SettingsRepository) bind ISettingsRepository::class

    // Use cases (settings-specific only; LogoutUseCase is in userModule)
    factoryOf(::GetThemeUseCase)
    factoryOf(::SaveThemeUseCase)
    factoryOf(::GetLanguageUseCase)
    factoryOf(::SaveLanguageUseCase)
    factoryOf(::GetNotificationsEnabledUseCase)
    factoryOf(::SaveNotificationsEnabledUseCase)

    // ViewModels
    viewModelOf(::SettingsViewModel)
    viewModelOf(::EditProfileViewModel)
}

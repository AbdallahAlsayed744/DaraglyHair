package com.daragly.daraglyhair.feature.user.di

import com.daragly.daraglyhair.feature.user.data.repository.UserRepository
import com.daragly.daraglyhair.feature.user.data.repository.local.UserLocalDataSource
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository
import com.daragly.daraglyhair.feature.user.domain.repository.local.IUserLocalDataSource
import com.daragly.daraglyhair.feature.user.domain.usecase.GetTokenUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.GetUserUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.LogoutUseCase
import com.daragly.daraglyhair.feature.user.domain.usecase.SaveUserUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userModule = module {
    // Local data source
    singleOf(::UserLocalDataSource) bind IUserLocalDataSource::class

    // Repository
    singleOf(::UserRepository) bind IUserRepository::class

    // Use cases
    factoryOf(::GetUserUseCase)
    factoryOf(::SaveUserUseCase)
    factoryOf(::GetTokenUseCase)
    factoryOf(::LogoutUseCase)
}

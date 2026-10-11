package com.daragly.daraglyhair.feature.home.di

import com.daragly.daraglyhair.feature.home.data.remote.HomeRemoteDataSource
import com.daragly.daraglyhair.feature.home.data.repository.HomeRepository
import com.daragly.daraglyhair.feature.home.domain.repository.IHomeRepository
import com.daragly.daraglyhair.feature.home.ui.HomeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule = module {
    // Remote Data Source
    singleOf(::HomeRemoteDataSource)
    
    // Repository
    singleOf(::HomeRepository) bind IHomeRepository::class
    
    // ViewModel
    factoryOf(::HomeViewModel)
}

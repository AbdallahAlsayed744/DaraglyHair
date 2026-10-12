package com.daragly.daraglyhair.feature.explore.di

import com.daragly.daraglyhair.feature.explore.ui.ExploreViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val exploreModule = module {
    // ExploreViewModel depends on IHomeRepository — already provided by homeModule
    factoryOf(::ExploreViewModel)
}

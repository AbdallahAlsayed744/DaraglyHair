package com.daragly.daraglyhair.feature.authentication.di

import com.daragly.daraglyhair.feature.authentication.data.remote.AuthRemoteDataSource
import com.daragly.daraglyhair.feature.authentication.data.repository.AuthRepository
import com.daragly.daraglyhair.feature.authentication.domain.interactors.ForgotPasswordUseCase
import com.daragly.daraglyhair.feature.authentication.domain.interactors.GetIsAuthenticatedUseCase
import com.daragly.daraglyhair.feature.authentication.domain.interactors.LoginUseCase
import com.daragly.daraglyhair.feature.authentication.domain.interactors.RegisterUseCase
import com.daragly.daraglyhair.feature.authentication.domain.interactors.ResetPasswordUseCase
import com.daragly.daraglyhair.feature.authentication.domain.repository.IAuthRepository
import com.daragly.daraglyhair.feature.authentication.ui.forgotpassword.ForgotPasswordViewModel
import com.daragly.daraglyhair.feature.authentication.ui.login.LoginViewModel
import com.daragly.daraglyhair.feature.authentication.ui.register.RegisterViewModel
import com.daragly.daraglyhair.feature.authentication.ui.resetpassword.ResetPasswordViewModel
import com.daragly.daraglyhair.feature.authentication.ui.splash.SplashViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    // Remote data source
    factoryOf(::AuthRemoteDataSource)

    // Repository
    single<IAuthRepository> { AuthRepository(get(), get(), get()) }

    // Use cases
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::ForgotPasswordUseCase)
    factoryOf(::ResetPasswordUseCase)
    factoryOf(::GetIsAuthenticatedUseCase)

    // ViewModels
    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ForgotPasswordViewModel)

    // ResetPasswordViewModel takes `email: String` param — injected from nav route
    viewModel { params ->
        ResetPasswordViewModel(email = params.get(), resetPasswordUseCase = get())
    }
}

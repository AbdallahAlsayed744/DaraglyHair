package com.daragly.daraglyhair.feature.authentication.ui.splash

import com.daragly.daraglyhair.common.domain.Resource
import com.daragly.daraglyhair.common.ui.navigation.IAuthGraph
import com.daragly.daraglyhair.common.ui.navigation.IMainGraph
import com.daragly.daraglyhair.common.ui.viewmodel.BaseViewModel
import com.daragly.daraglyhair.feature.authentication.domain.interactors.GetIsAuthenticatedUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

// ── Contract ─────────────────────────────────────────────────────────────────

data class SplashState(val isLoading: Boolean = true)

sealed interface SplashAction
sealed interface SplashEvent

// ── ViewModel ────────────────────────────────────────────────────────────────

class SplashViewModel(
    private val getIsAuthenticated: GetIsAuthenticatedUseCase,
) : BaseViewModel<SplashState, SplashAction, SplashEvent>(SplashState()) {

    init { checkAuth() }

    override fun onAction(action: SplashAction) = Unit   // no user actions on splash

    private fun checkAuth() = viewModelScope.launch {
        delay(2_000)
        when (val result = getIsAuthenticated.invoke(Unit)) {
            is Resource.Success -> {
                if (result.model) {
                    fireNavigate(IMainGraph.Home, builder = {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    })
                } else {
                    fireNavigate(IAuthGraph.Login, builder = {
                        popUpTo(IAuthGraph.AuthGraph) { inclusive = false }
                    })
                }
            }
            else -> {
                fireNavigate(IAuthGraph.Login, builder = {
                    popUpTo(IAuthGraph.AuthGraph) { inclusive = false }
                })
            }
        }
    }
}

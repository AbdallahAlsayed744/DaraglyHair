package com.daragly.daraglyhair.common.ui.navigation

import androidx.navigation.NavOptionsBuilder

// Marker interfaces so destinations and graphs are type-safe
interface IDestination
interface IGraph

sealed class NavigationEvent {
    data class Navigate(
        val destination: IDestination,
        val builder: NavOptionsBuilder.() -> Unit = {},
    ) : NavigationEvent()

    data object NavigateUp : NavigationEvent()
}

package com.daragly.daraglyhair.common.ui.navigation

import androidx.navigation.NavOptionsBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class Navigator(override val startGraph: IGraph) : INavigator {

    private val _channel = Channel<NavigationEvent>()
    override val navigationEvent = _channel.receiveAsFlow()

    override suspend fun navigate(
        destination: IDestination,
        builder: NavOptionsBuilder.() -> Unit,
    ) = _channel.send(NavigationEvent.Navigate(destination, builder))

    override suspend fun navigateUp() =
        _channel.send(NavigationEvent.NavigateUp)
}

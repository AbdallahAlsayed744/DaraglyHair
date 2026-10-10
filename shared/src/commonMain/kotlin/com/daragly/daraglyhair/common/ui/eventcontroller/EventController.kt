package com.daragly.daraglyhair.common.ui.eventcontroller

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

interface IEventController<T> {
    val event: kotlinx.coroutines.flow.Flow<T>
    suspend fun emit(event: T)
}

class EventController<T> : IEventController<T> {
    private val _channel = Channel<T>(Channel.BUFFERED)
    override val event = _channel.receiveAsFlow()
    override suspend fun emit(event: T) = _channel.send(event)
}

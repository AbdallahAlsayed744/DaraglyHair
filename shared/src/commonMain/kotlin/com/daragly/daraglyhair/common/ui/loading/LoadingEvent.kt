package com.daragly.daraglyhair.common.ui.loading

sealed interface ILoadingEvent {
    /** Mirrors ILoadingEvent.CircularProgressIndicator from Delivery-User */
    data class CircularProgressIndicator(val isLoading: Boolean) : ILoadingEvent
}

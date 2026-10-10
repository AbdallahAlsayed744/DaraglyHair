package com.daragly.daraglyhair.common.ui.message

enum class HallaakMessageType { SUCCESS, ERROR, WARNING, INFO }

sealed interface IMessageEvent {
    /** Mirrors IMessageEvent.Snackbar from Delivery-User */
    data class Snackbar(
        val message: String,
        val type: HallaakMessageType = HallaakMessageType.INFO,
    ) : IMessageEvent
}

package com.daragly.daraglyhair.common.data.repository.remote

import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Maps server-side field error keys to app-level enum constants.
 * Add new entries here as the BarberHub API introduces new validation fields.
 */
@Serializable
enum class ErrorKeyEnum : IErrorKeyEnum {
    @SerialName("phone")
    PHONE_NUMBER,

    @SerialName("email")
    EMAIL,

    @SerialName("password")
    PASSWORD,

    @SerialName("name")
    NAME,

    @SerialName("code")
    OTP,

    @SerialName("confirmationPassword")
    CONFIRMATION_PASSWORD,

    @SerialName("message")
    USER_MESSAGE,

    UNKNOWN;
}

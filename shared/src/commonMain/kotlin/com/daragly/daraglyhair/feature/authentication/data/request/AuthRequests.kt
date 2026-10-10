package com.daragly.daraglyhair.feature.authentication.data.request

import com.daragly.daraglyhair.common.data.repository.remote.ErrorKeyEnum
import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import com.daragly.daraglyhair.common.domain.exceptions.RequestErrorKeyValues
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
) {
    fun validateFields(): Map<IErrorKeyEnum, RequestErrorKeyValues> {
        val errors = mutableMapOf<IErrorKeyEnum, RequestErrorKeyValues>()
        if (!email.contains("@") || email.length < 5)
            errors[ErrorKeyEnum.EMAIL] = RequestErrorKeyValues.EMAIL_VALIDATION
        if (password.length < 8)
            errors[ErrorKeyEnum.PASSWORD] = RequestErrorKeyValues.PASSWORD_VALIDATION
        return errors
    }
}

@Serializable
data class RegisterRequest(
    @SerialName("firstName")    val firstName: String,
    @SerialName("lastName")     val lastName: String,
    @SerialName("email")        val email: String,
    @SerialName("userName")     val userName: String,
    @SerialName("password")     val password: String,
    @SerialName("confirmPassword") val confirmPassword: String,
    @SerialName("phoneNumber")  val phoneNumber: String,
) {
    fun validateFields(): Map<IErrorKeyEnum, RequestErrorKeyValues> {
        val errors = mutableMapOf<IErrorKeyEnum, RequestErrorKeyValues>()
        if (firstName.trim().length < 2)
            errors[ErrorKeyEnum.NAME] = RequestErrorKeyValues.NAME_VALIDATION
        if (!email.contains("@") || email.length < 5)
            errors[ErrorKeyEnum.EMAIL] = RequestErrorKeyValues.EMAIL_VALIDATION
        if (password.length < 8)
            errors[ErrorKeyEnum.PASSWORD] = RequestErrorKeyValues.PASSWORD_VALIDATION
        if (password != confirmPassword)
            errors[ErrorKeyEnum.CONFIRMATION_PASSWORD] = RequestErrorKeyValues.CONFIRMATION_PASSWORD
        if (phoneNumber.trim().length < 9)
            errors[ErrorKeyEnum.PHONE_NUMBER] = RequestErrorKeyValues.PHONE_VALIDATION
        return errors
    }
}

@Serializable
data class ForgotPasswordRequest(
    @SerialName("email") val email: String,
)

@Serializable
data class ResetPasswordRequest(
    @SerialName("email")    val email: String,
    @SerialName("password") val password: String,
    @SerialName("token")    val token: String,
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("token")        val token: String,
    @SerialName("refreshToken") val refreshToken: String,
)

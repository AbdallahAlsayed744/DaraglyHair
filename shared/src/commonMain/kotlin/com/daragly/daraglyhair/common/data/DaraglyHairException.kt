package com.daragly.daraglyhair.common.data

import com.daragly.daraglyhair.common.domain.exceptions.IErrorKeyEnum
import com.daragly.daraglyhair.common.domain.exceptions.RequestErrorKeyValues

/**
 * Custom exception hierarchy for DaraglyHair.
 * Mirrors the same sealed class structure used in Delivery-User.
 */
sealed class DaraglyHairException(override val message: String? = null) : Exception(message) {

    sealed class Network(override val message: String? = null) : DaraglyHairException(message) {
        /** No connectivity, DNS failure, or socket timeout — safe to retry. */
        data class Repeatable(override val message: String? = null) : Network(message)

        /** Any unclassified network-level error. */
        data class Unhandled(val errorCode: Int, override val message: String? = null) :
            Network(message = "Network error [code=$errorCode]: $message")
    }

    sealed class Client(override val message: String? = null) : DaraglyHairException(message) {
        /** HTTP 401 */
        data class UnAuthorized(override val message: String? = null) :
            Client(message = "Unauthorized")

        /** HTTP 404 */
        data class NotFound(override val message: String? = null) :
            Client(message = "Not Found")

        /** HTTP 422 — field-level validation errors returned by the server. */
        data class ResponseValidation(
            val errors: Map<IErrorKeyEnum, String>,
            override val message: String? = null,
        ) : Client(message)

        /** Any other 4xx error. */
        data class Unhandled(val errorCode: Int, override val message: String? = null) :
            Client(message = "Client error [code=$errorCode]: $message")
    }

    sealed class Server(override val message: String? = null) : DaraglyHairException(message) {
        /** HTTP 500 */
        data class InternalServerError(
            val httpErrorCode: Int,
            override val message: String? = null,
        ) : Server(message = "Internal server error [code=$httpErrorCode]: $message")
    }

    sealed class Local(override val message: String? = null) : DaraglyHairException(message) {
        /** Client-side form / request validation failure. */
        data class RequestValidation(
            override val message: String? = null,
            val errors: Map<IErrorKeyEnum, RequestErrorKeyValues> = emptyMap(),
        ) : Local(message)

        /** DataStore / IO failure. */
        data class IOOperation(override val message: String? = null) : Local(message)

        /** Any other local storage error. */
        data class Unhandled(val errorCode: Int, override val message: String? = null) :
            Local(message = "Local error [code=$errorCode]: $message")
    }

    /** Catch-all for truly unexpected throwables. */
    data class UnKnown(override val message: String? = null) : DaraglyHairException(message)
}

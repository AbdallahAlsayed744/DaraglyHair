package com.daragly.daraglyhair.common.data.repository.remote

import com.daragly.daraglyhair.common.data.DaraglyHairException
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "https://barberhub-api.runasp.net/"

/**
 * Builds the shared Ktor [HttpClient] using a platform-provided [engine].
 *
 * @param engine    Platform HTTP engine (Android / Darwin)
 * @param json      Shared [Json] instance for content-negotiation
 * @param getToken  Suspending lambda that returns the Bearer token (null = no auth header)
 * @param getLang   Suspending lambda that returns the current language code ("en" / "ar")
 */
fun provideHttpClient(
    engine: HttpClientEngineFactory<*>,
    json: Json,
    getToken: suspend () -> String?,
    getLang: suspend () -> String,
): HttpClient = HttpClient(engine) {
    expectSuccess = true

    install(ContentNegotiation) { json(json) }

    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.SIMPLE
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 60_000
        socketTimeoutMillis = 20_000
    }

    defaultRequest {
        url(BASE_URL)
        contentType(ContentType.Application.Json)
    }

    HttpResponseValidator {
        handleResponseExceptionWithRequest { exception, _ ->
            val lang = try {
                getLang()
            } catch (_: Exception) {
                "en"
            }
            throw when (exception) {
                is ResponseException ->
                    handleResponseException(exception.response, json, lang)

                else -> {
                    val msg = exception.message ?: "Unknown error"
                    when {
                        msg.contains("UnknownHostException", ignoreCase = true) ||
                                msg.contains("ConnectException", ignoreCase = true) ||
                                msg.contains("Network is unreachable", ignoreCase = true) ->
                            if (lang == "ar")
                                DaraglyHairException.Network.Repeatable("تحقق من اتصالك بالإنترنت")
                            else
                                DaraglyHairException.Network.Repeatable("Check your internet connection")

                        msg.contains("SocketTimeoutException", ignoreCase = true) ||
                                msg.contains(
                                    "timed out",
                                    ignoreCase = true
                                ) || msg.contains("ConnectTimeoutException", ignoreCase = true) ->
                            DaraglyHairException.Network.Repeatable("Request timed out")

                        else -> DaraglyHairException.UnKnown(msg)
                    }
                }
            }
        }
    }
}

private suspend fun handleResponseException(
    response: HttpResponse,
    json: Json,
    lang: String,
): DaraglyHairException = when (val code = response.status.value) {
    HttpStatusCode.Unauthorized.value ->
        DaraglyHairException.Client.UnAuthorized()

    HttpStatusCode.UnprocessableEntity.value -> {
        val body = response.bodyAsText()
        val errorResponse = runCatching {
            json.decodeFromString<APIErrorResponse>(body)
        }.getOrNull()

        if (errorResponse != null) {
            DaraglyHairException.Client.ResponseValidation(
                errors = errorResponse.errors
                    ?.mapNotNull { (key, value) ->
                        if (key == ErrorKeyEnum.UNKNOWN) null else key to value.first()
                    }
                    ?.toMap() ?: emptyMap(),
                message = errorResponse.message,
            )
        } else {
            DaraglyHairException.Client.Unhandled(errorCode = code, message = body)
        }
    }

    HttpStatusCode.NotFound.value ->
        DaraglyHairException.Client.NotFound()

    HttpStatusCode.InternalServerError.value ->
        DaraglyHairException.Server.InternalServerError(
            httpErrorCode = code,
            message = if (lang == "ar")
                "حدث خطأ من جانبنا. يُرجى المحاولة مرة أخرى لاحقًا."
            else
                "Something went wrong on our side. Please try again later.",
        )

    else ->
        DaraglyHairException.Client.Unhandled(
            errorCode = code,
            message = response.status.description,
        )
}

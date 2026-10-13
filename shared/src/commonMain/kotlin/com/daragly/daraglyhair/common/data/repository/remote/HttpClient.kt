package com.daragly.daraglyhair.common.data.repository.remote

import com.daragly.daraglyhair.common.data.DaraglyHairException
import com.daragly.daraglyhair.feature.authentication.data.dto.TokenDto
import com.daragly.daraglyhair.feature.authentication.data.remote.AuthRemoteDataSource
import com.daragly.daraglyhair.feature.authentication.data.request.RefreshTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val BASE_URL = "https://barberhub-api.runasp.net/"

/**
 * Builds the shared Ktor [HttpClient] using a platform-provided [engine].
 *
 * @param engine           Platform HTTP engine (Android / Darwin)
 * @param json             Shared [Json] instance for content-negotiation
 * @param publicPaths      Path fragments that must never carry an Authorization header
 * @param getToken         Suspending lambda returning the stored access token (null = anonymous)
 * @param getRefreshToken  Suspending lambda returning the stored refresh token (null = anonymous)
 * @param saveTokens       Persists a freshly issued token pair after a successful refresh
 * @param clearTokens      Drops the whole session when the refresh token is itself rejected
 * @param onSessionExpired Notifies the app that the session is gone and it must show login again
 * @param getLang          Returns the current language code ("en" / "ar"); must not suspend
 */
fun provideHttpClient(
    engine: HttpClientEngineFactory<*>,
    json: Json,
    publicPaths: List<String>,
    getToken: suspend () -> String?,
    getRefreshToken: suspend () -> String?,
    saveTokens: suspend (accessToken: String, refreshToken: String) -> Unit,
    clearTokens: suspend () -> Unit,
    onSessionExpired: suspend () -> Unit,
    getLang: () -> String,
): HttpClient {

    // ── Traced wrappers around the storage lambdas (values are never printed) ──
    val loggedGetToken: suspend () -> String? = {
        getToken().also { DebugLog.d { "AUTH storage getToken -> present=${!it.isNullOrBlank()}" } }
    }
    val loggedGetRefreshToken: suspend () -> String? = {
        getRefreshToken().also { DebugLog.d { "AUTH storage getRefreshToken -> present=${!it.isNullOrBlank()}" } }
    }
    val loggedSaveTokens: suspend (String, String) -> Unit = { access, refresh ->
        DebugLog.d { "AUTH storage saveTokens: access=${access.isNotBlank()} refresh=${refresh.isNotBlank()}" }
        saveTokens(access, refresh)
        DebugLog.d { "AUTH storage after save: refresh readable=${!getRefreshToken().isNullOrBlank()}" }
    }

    /** Storage teardown first, then one navigation signal — every unrecoverable path ends here. */
    val endSession: suspend () -> Unit = {
        DebugLog.d { "AUTH storage clearTokens CALLED" }
        clearTokens()
        onSessionExpired()
    }

    return HttpClient(engine) {

        // We do the status check ourselves in HttpResponseValidator below.
        expectSuccess = false

        // 1) Default request
        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)
            header("tenant", "root")
            header("Accept-Language", getLang())
        }

        // 2) JSON
        install(ContentNegotiation) { json(json) }

        // 3) Auth
        install(Auth) {
            bearer {
                loadTokens {
                    val access = loggedGetToken()
                    val refresh = loggedGetRefreshToken()
                    DebugLog.d { "AUTH loadTokens: access=${!access.isNullOrBlank()} refresh=${!refresh.isNullOrBlank()}" }
                    if (access.isNullOrBlank()) null
                    else BearerTokens(access, refresh.orEmpty())
                }

                refreshTokens {
                    DebugLog.d { "AUTH refreshTokens CALLED (oldTokens present=${oldTokens != null})" }
                    val stored = loggedGetRefreshToken()
                    if (stored.isNullOrBlank()) {
                        DebugLog.d { "AUTH no stored refresh token -> ending session" }
                        endSession()
                        return@refreshTokens null
                    }
                    try {
                        DebugLog.d { "AUTH calling refresh endpoint: ${AuthRemoteDataSource.REFRESH_ENDPOINT}" }
                        val dto = client.post(AuthRemoteDataSource.REFRESH_ENDPOINT) {
                            markAsRefreshTokenRequest()
                            setBody(
                                RefreshTokenRequest(
                                    token = oldTokens?.accessToken ?: loggedGetToken().orEmpty(),
                                    refreshToken = stored,
                                ),
                            )
                        }.body<TokenDto>()

                        val newAccess = dto.token.orEmpty()
                        val newRefresh = dto.refreshToken.orEmpty()
                        DebugLog.d { "AUTH refresh response: access=${newAccess.isNotBlank()} refresh=${newRefresh.isNotBlank()}" }
                        if (newAccess.isBlank() || newRefresh.isBlank()) {
                            DebugLog.d { "AUTH refresh returned blank tokens" }
                            null
                        } else {
                            loggedSaveTokens(newAccess, newRefresh)
                            BearerTokens(newAccess, newRefresh)
                        }
                    } catch (exception: Exception) {
                        if (exception.isRejectedRefresh()) {
                            DebugLog.d { "AUTH refresh token rejected -> ending session" }
                            endSession()
                        } else {
                            DebugLog.d { "AUTH refresh failed (transient): ${exception::class.simpleName}" }
                        }
                        null
                    }
                }

                // Prefix match (not `contains`) so a public fragment can never accidentally
                // mark an unrelated protected endpoint as anonymous.
                sendWithoutRequest { request ->
                    val path = request.url.encodedPath.trimStart('/')
                    publicPaths.none { path.startsWith(it.trimStart('/')) }
                }
            }
        }

        // 4) Logging
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.SIMPLE
            sanitizeHeader { it == HttpHeaders.Authorization }
        }

        // 5) Timeout
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 20_000
        }

        // 6) Validation (turns non-2xx into exceptions, maps them to your domain errors)
        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess()) {
                    throw ResponseException(response, response.bodyAsText())
                }
            }

            handleResponseExceptionWithRequest { exception, _ ->
                val lang = getLang()
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
                                    msg.contains("timed out", ignoreCase = true) ||
                                    msg.contains("ConnectTimeoutException", ignoreCase = true) ->
                                DaraglyHairException.Network.Repeatable("Request timed out")

                            else -> DaraglyHairException.UnKnown(msg)
                        }
                    }
                }
            }
        }
    }
}

/**
 * A refresh that the server actively refuses can never succeed on retry, so the session must end.
 * `/api/tokens/refresh` answers a token mismatch with 400 as often as with 401, hence both.
 * Network, timeout and 5xx failures stay retryable and must not clear the session.
 */
private fun Throwable.isRejectedRefresh(): Boolean = when (this) {
    is DaraglyHairException.Client.UnAuthorized -> true
    is DaraglyHairException.Client.Unhandled ->
        errorCode == HttpStatusCode.BadRequest.value || errorCode == HttpStatusCode.Forbidden.value

    else -> false
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

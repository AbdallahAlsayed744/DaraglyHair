package com.daragly.daraglyhair.common.data.repository.remote

import com.daragly.daraglyhair.common.ui.eventcontroller.IEventController
import com.daragly.daraglyhair.common.ui.session.ISessionEvent
import com.daragly.daraglyhair.feature.user.domain.repository.IUserRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

/**
 * Single owner of the authenticated session.
 *
 * Every code path that obtains or drops tokens (login, refresh, sign-out, session expiry)
 * goes through here so that DataStore state and the bearer provider's in-memory token
 * cache can never drift apart.
 */
class SessionManager(
    private val client: HttpClient,
    private val userRepository: IUserRepository,
    private val sessionEvents: IEventController<ISessionEvent>,
) {

    suspend fun accessToken(): String? =
        userRepository.getToken().takeIf { it.isNotBlank() }

    suspend fun refreshToken(): String? =
        userRepository.getRefreshToken().takeIf { it.isNotBlank() }

    suspend fun saveSession(accessToken: String, refreshToken: String) {
        userRepository.saveToken(accessToken)
        if (refreshToken.isNotBlank()) userRepository.saveRefreshToken(refreshToken)
        userRepository.saveIsAuthenticated(true)
        clearCachedTokens()
    }

    suspend fun clearSession() {
        userRepository.deleteToken()
        userRepository.deleteRefreshToken()
        userRepository.deleteUser()
        userRepository.deleteIsAuthenticated()
        clearCachedTokens()
    }

    suspend fun notifySessionExpired() {
        sessionEvents.emit(ISessionEvent.Expired)
    }

    /**
     * `loadTokens` is cached inside [BearerAuthProvider], so token storage written by any other
     * path stays invisible to the client until this forces the next request to re-read it.
     */
    private fun clearCachedTokens() {
        client.authProvider<BearerAuthProvider>()?.clearToken()
    }
}

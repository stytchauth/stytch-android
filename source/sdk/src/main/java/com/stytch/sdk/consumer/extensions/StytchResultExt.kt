
@file:Suppress("ktlint:standard:filename")

package com.stytch.sdk.consumer.extensions

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.IAuthData
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.withContext

private fun <T : IAuthData> saveSession(
    result: T,
    sessionStorage: ConsumerSessionStorage,
) {
    result.apply {
        try {
            sessionStorage.updateSession(sessionToken, sessionJwt, session)
            sessionStorage.user = user
        } catch (_: Exception) {
        }
    }
}

/**
 * Starts session update in background
 */
internal fun <T : IAuthData> StytchResult<T>.launchSessionUpdater(
    dispatchers: StytchDispatchers,
    sessionStorage: ConsumerSessionStorage,
) {
    if (this is StytchResult.Success) {
        // save session data
        saveSession(this.value, sessionStorage)
        // start auto session update
        SessionAutoUpdater.startSessionUpdateJob(
            dispatchers = dispatchers,
            updateSession = {
                withContext(dispatchers.io) {
                    StytchApi.Sessions.authenticate(
                        if (StytchClient.configurationManager.options.enableAutomaticSessionExtension) {
                            StytchClient.configurationManager.options.sessionDurationMinutes
                        } else {
                            null
                        },
                    )
                }
            },
            saveSession = { result ->
                if (result is IAuthData) {
                    saveSession(result, sessionStorage)
                }
            },
            clearSession = {
                sessionStorage.revoke()
            },
            getCurrentSessionId = {
                sessionStorage.session?.sessionId
            },
        )
    }
}

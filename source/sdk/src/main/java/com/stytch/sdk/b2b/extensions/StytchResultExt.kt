package com.stytch.sdk.b2b.extensions

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.IB2BAuthData
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import kotlinx.coroutines.withContext

private fun <T : IB2BAuthData> saveSession(
    result: T,
    sessionStorage: B2BSessionStorage,
) {
    result.apply {
        try {
            sessionStorage.updateSession(
                sessionToken = sessionToken,
                sessionJwt = sessionJwt,
                session = memberSession,
                intermediateSessionToken = null,
            )
            sessionStorage.member = member
        } catch (_: Exception) {
        }
    }
}

private fun <T : IB2BAuthDataWithMFA> saveSession(
    result: T,
    sessionStorage: B2BSessionStorage,
) {
    result.apply {
        try {
            sessionStorage.updateSession(
                sessionToken = if (memberSession != null) sessionToken else null,
                sessionJwt = if (memberSession != null) sessionJwt else null,
                session = memberSession,
                intermediateSessionToken = intermediateSessionToken,
            )
            sessionStorage.member = member
        } catch (_: Exception) {
        }
    }
}

/**
 * Starts session update in background
 */
internal fun <T : CommonAuthenticationData> StytchResult<T>.launchSessionUpdater(
    dispatchers: StytchDispatchers,
    sessionStorage: B2BSessionStorage,
) {
    if (this is StytchResult.Success) {
        // save session data
        if (value is IB2BAuthData) {
            saveSession(value, sessionStorage)
        } else if (value is IB2BAuthDataWithMFA) {
            saveSession(value, sessionStorage)
        }
        // start auto session update
        SessionAutoUpdater.startSessionUpdateJob(
            dispatchers = dispatchers,
            updateSession = {
                withContext(dispatchers.io) {
                    StytchB2BApi.Sessions.authenticate(
                        if (StytchB2BClient.configurationManager.options.enableAutomaticSessionExtension) {
                            StytchB2BClient.configurationManager.options.defaultSessionDuration
                        } else {
                            null
                        },
                    )
                }
            },
            saveSession = { result ->
                if (result is IB2BAuthData) {
                    saveSession(result, sessionStorage)
                } else if (result is IB2BAuthDataWithMFA) {
                    saveSession(result, sessionStorage)
                }
            },
            clearSession = {
                sessionStorage.revoke()
            },
            getCurrentSessionId = {
                sessionStorage.memberSession?.memberSessionId
            },
        )
    }
}

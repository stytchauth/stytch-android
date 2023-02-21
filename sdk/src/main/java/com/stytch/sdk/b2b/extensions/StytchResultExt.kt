// ktlint-disable filename
package com.stytch.sdk.b2b.extensions

import com.stytch.sdk.b2b.network.IB2BAuthData
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sessions.SessionAutoUpdater
import kotlinx.coroutines.withContext

private fun <T : IB2BAuthData> saveSession(result: T, sessionStorage: B2BSessionStorage) {
    result.apply {
        try {
            sessionStorage.updateSession(sessionToken, sessionJwt, memberSession)
            sessionStorage.member = member
        } catch (_: Exception) {
        }
    }
}

/**
 * Starts session update in background
 */
internal fun <T : IB2BAuthData> StytchResult<T>.launchSessionUpdater(
    dispatchers: StytchDispatchers,
    sessionStorage: B2BSessionStorage
) {
    if (this is StytchResult.Success) {
        // save session data
        saveSession(this.value, sessionStorage)
        // start auto session update
        SessionAutoUpdater.startSessionUpdateJob(
            dispatchers = dispatchers,
            updateSession = {
                withContext(dispatchers.io) {
                    StytchB2BApi.Sessions.authenticate(null)
                }
            },
            saveSession = { result ->
                if (result is IB2BAuthData) {
                    saveSession(result, sessionStorage)
                }
            }
        )
    }
}

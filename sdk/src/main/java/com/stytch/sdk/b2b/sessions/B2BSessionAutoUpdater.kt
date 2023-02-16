package com.stytch.sdk.b2b.sessions

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.b2b.network.IB2BAuthData
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import kotlin.math.pow
import kotlin.random.Random
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SECOND = 1000L
private const val MINUTE = 60L * SECOND

private const val DEFAULT_DELAY = 3 * MINUTE
private const val MAXIMUM_DELAY = 5 * MINUTE

private const val MINIMUM_RANDOM_MILLIS = -17L
private const val MAXIMUM_RANDOM_MILLIS = 17L
private const val MAXIMUM_BACKOFF_DELAY = 32 * SECOND

internal object B2BSessionAutoUpdater {
    @VisibleForTesting
    internal var sessionUpdateJob: Job? = null
    private var n = 0
    private var sessionUpdateDelay: Long = DEFAULT_DELAY
    private var backoffStartMillis: Long = 0

    fun startSessionUpdateJob(dispatchers: StytchDispatchers, sessionStorage: B2BSessionStorage) {
        // prevent multiple update jobs running
        stopSessionUpdateJob()
        sessionUpdateJob = GlobalScope.launch(dispatchers.io) {
            while (true) {
                // wait before another update request
                delay(sessionUpdateDelay)
                // request session update from backend
                val sessionResult = updateSession(dispatchers)
                // save session data in SessionStorage if call successful
                if (sessionResult is StytchResult.Success) {
                    // reset exponential backoff delay
                    resetDelay()
                    // save session
                    sessionResult.saveSession(sessionStorage)
                } else {
                    // set backoff start if not set
                    if (backoffStartMillis <= 0) {
                        backoffStartMillis = System.currentTimeMillis()
                    }
                    // if delay reached max delay stop exponential backoff
                    if (System.currentTimeMillis() - backoffStartMillis > MAXIMUM_DELAY - DEFAULT_DELAY) {
                        resetDelay()
                        // stop auto updater/ exit while loop
                        break
                    } else {
                        // set exponential delay
                        sessionUpdateDelay = minOf(
                            (2.0.pow(n) + Random.nextLong(MINIMUM_RANDOM_MILLIS, MAXIMUM_RANDOM_MILLIS)).toLong(),
                            MAXIMUM_BACKOFF_DELAY
                        )
                        n++
                    }
                }
            }
        }
    }

    private fun resetDelay() {
        n = 0
        sessionUpdateDelay = DEFAULT_DELAY
        backoffStartMillis = 0
    }

    @VisibleForTesting
    internal fun stopSessionUpdateJob() {
        sessionUpdateJob?.cancel()
        sessionUpdateJob = null
    }

    private suspend fun updateSession(dispatchers: StytchDispatchers): StytchResult<IB2BAuthData> =
        withContext(dispatchers.io) {
            StytchB2BApi.Sessions.authenticate(null)
        }
}

// save session data
internal fun <T : IB2BAuthData> StytchResult<T>.saveSession(sessionStorage: B2BSessionStorage): StytchResult<T> {
    if (this is StytchResult.Success) {
        value.apply {
            try {
                sessionStorage.updateSession(sessionToken, sessionJwt, session)
                sessionStorage.member = member
            } catch (ex: Exception) {
                return StytchResult.Error(com.stytch.sdk.common.StytchExceptions.Critical(ex))
            }
        }
    }
    return this
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
        saveSession(sessionStorage)
        // start auto session update
        B2BSessionAutoUpdater.startSessionUpdateJob(dispatchers, sessionStorage)
    }
}
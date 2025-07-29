package com.stytch.sdk.common.sessions

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.network.models.CommonAuthenticationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.random.Random

private const val SECOND = 1000L
private const val MINUTE = 60L * SECOND

private const val DEFAULT_DELAY = 3 * MINUTE
private const val MAXIMUM_DELAY = 5 * MINUTE

private const val MINIMUM_RANDOM_MILLIS = -17L
private const val MAXIMUM_RANDOM_MILLIS = 17L
private const val MAXIMUM_BACKOFF_DELAY = 32 * SECOND

internal object SessionAutoUpdater {
    @VisibleForTesting
    internal var sessionUpdateJob: Job? = null
    private var n = 0
    private var sessionUpdateDelay: Long = DEFAULT_DELAY
    private var backoffStartMillis: Long = 0
    private val scope = CoroutineScope(SupervisorJob())

    fun startSessionUpdateJob(
        dispatchers: StytchDispatchers,
        updateSession: suspend () -> StytchResult<CommonAuthenticationData>,
        saveSession: (CommonAuthenticationData) -> Unit,
        clearSession: () -> Unit = {},
        getCurrentSessionId: () -> String? = { null },
    ) {
        // prevent multiple update jobs running
        stopSessionUpdateJob()
        sessionUpdateJob =
            scope.launch(dispatchers.io) {
                while (true) {
                    // wait before another update request
                    delay(sessionUpdateDelay)
                    val initialSessionId = getCurrentSessionId()
                    val sessionResult = updateSession()
                    if (initialSessionId != getCurrentSessionId()) {
                        // The session was updated out from under us while the request was in flight;
                        // discard the response and retry
                        sessionUpdateDelay = 0
                        continue
                    }
                    when (sessionResult) {
                        is StytchResult.Success -> {
                            resetDelay()
                            saveSession(sessionResult.value)
                        }
                        is StytchResult.Error -> {
                            if (initialSessionId != getCurrentSessionId()) {
                                // The session was updated out from under us while the request was in flight;
                                // discard the response and retry
                                sessionUpdateDelay = 0
                                continue
                            }
                            if ((sessionResult.exception as? StytchAPIError)?.isUnrecoverableError() == true) {
                                clearSession()
                            }
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
                                sessionUpdateDelay =
                                    minOf(
                                        (
                                            2.0.pow(n) +
                                                Random.nextLong(
                                                    MINIMUM_RANDOM_MILLIS,
                                                    MAXIMUM_RANDOM_MILLIS,
                                                )
                                        ).toLong(),
                                        MAXIMUM_BACKOFF_DELAY,
                                    )
                                n++
                            }
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
}

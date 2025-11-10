package com.stytch.sdk.consumer.sessions

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchFailedToDecryptDataError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.stytchObjectMapper
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.SessionAttestResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.models.SessionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class SessionsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.Sessions,
) : Sessions {
    private val callbacks = mutableListOf<(StytchObjectInfo<SessionData>) -> Unit>()

    internal inline fun <reified T : Any> maybeForceClearSession(
        result: StytchResult.Error,
        forceClear: Boolean = false,
    ): StytchResult<T> =
        if (forceClear || result.exception is StytchAPIError && result.exception.isUnrecoverableError()) {
            try {
                sessionStorage.revoke()
                result
            } catch (ex: Exception) {
                StytchResult.Error(StytchInternalError(ex))
            }
        } else {
            result
        }

    override val onChange: StateFlow<StytchObjectInfo<SessionData>> =
        combine(sessionStorage.sessionFlow, sessionStorage.lastValidatedAtFlow, ::stytchObjectMapper)
            .stateIn(
                externalScope,
                SharingStarted.WhileSubscribed(),
                StytchObjectInfo.Loading,
            )

    init {
        externalScope.launch {
            onChange.collect {
                callbacks.forEach { callback ->
                    callback(it)
                }
            }
        }
    }

    override fun onChange(callback: (StytchObjectInfo<SessionData>) -> Unit) {
        callbacks.add(callback)
    }

    override val sessionToken: String?
        get() {
            try {
                return sessionStorage.sessionToken
            } catch (ex: Exception) {
                throw StytchFailedToDecryptDataError(ex)
            }
        }

    override val sessionJwt: String?
        get() {
            try {
                return sessionStorage.sessionJwt
            } catch (ex: Exception) {
                throw StytchFailedToDecryptDataError(ex)
            }
        }

    override fun getSync(): SessionData? = sessionStorage.session

    override suspend fun authenticate(authParams: Sessions.AuthParams): AuthResponse =
        withContext(dispatchers.io) {
            val initialSessionId = sessionStorage.session?.sessionId
            val result = api.authenticate(authParams.sessionDurationMinutes)
            if (initialSessionId != sessionStorage.session?.sessionId) {
                // The session was updated out from under us while the request was in flight;
                // discard the response and retry
                return@withContext authenticate(authParams)
            }
            return@withContext when (result) {
                is StytchResult.Success -> {
                    result.apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
                }
                is StytchResult.Error -> {
                    if (initialSessionId != sessionStorage.session?.sessionId) {
                        // The session was updated out from under us while the request was in flight;
                        // discard the response and retry
                        return@withContext authenticate(authParams)
                    }
                    // Session was NOT updated, but was an error, so maybe clear session
                    maybeForceClearSession(result, false)
                }
            }
        }

    override fun authenticate(
        authParams: Sessions.AuthParams,
        callback: (AuthResponse) -> Unit,
    ) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(authParams)
            // change to main thread to call callback
            callback(result)
        }
    }

    override fun authenticateCompletable(authParams: Sessions.AuthParams): CompletableFuture<AuthResponse> =
        externalScope
            .async {
                authenticate(authParams)
            }.asCompletableFuture()

    override suspend fun revoke(params: Sessions.RevokeParams): BaseResponse =
        withContext(dispatchers.io) {
            var result = api.revoke()
            try {
                when (result) {
                    is StytchResult.Success -> sessionStorage.revoke()
                    is StytchResult.Error -> {
                        result = maybeForceClearSession<BasicData>(result, params.forceClear)
                    }
                }
                result
            } catch (e: Exception) {
                StytchResult.Error(StytchInternalError(e))
            }
        }

    override fun revoke(
        params: Sessions.RevokeParams,
        callback: (BaseResponse) -> Unit,
    ) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = revoke(params)
            // change to main thread to call callback
            callback(result)
        }
    }

    override fun revokeCompletable(params: Sessions.RevokeParams): CompletableFuture<BaseResponse> =
        externalScope
            .async {
                revoke(params)
            }.asCompletableFuture()

    /**
     * @throws StytchInternalError if failed to save data
     */
    override fun updateSession(
        sessionToken: String,
        sessionJwt: String?,
    ) {
        try {
            sessionStorage.updateSession(sessionToken, sessionJwt)
        } catch (ex: Exception) {
            throw StytchInternalError(ex)
        }
    }

    override suspend fun attest(params: Sessions.AttestParams): SessionAttestResponse =
        withContext(dispatchers.io) {
            api
                .attest(
                    profileId = params.profileId,
                    token = params.token,
                    sessionDurationMinutes = params.sessionDurationMinutes,
                    sessionJwt = params.sessionJwt,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun attest(
        params: Sessions.AttestParams,
        callback: (SessionAttestResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(attest(params))
        }
    }

    override fun attestCompletable(params: Sessions.AttestParams): CompletableFuture<SessionAttestResponse> =
        externalScope.async { attest(params) }.asCompletableFuture()
}

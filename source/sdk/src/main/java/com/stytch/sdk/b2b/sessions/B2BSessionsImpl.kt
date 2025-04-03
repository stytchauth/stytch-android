package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.SessionExchangeResponse
import com.stytch.sdk.b2b.SessionsAuthenticateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchAPIError
import com.stytch.sdk.common.errors.StytchFailedToDecryptDataError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.stytchObjectMapper
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

internal class B2BSessionsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Sessions,
) : B2BSessions {
    private val callbacks = mutableListOf<(StytchObjectInfo<B2BSessionData>) -> Unit>()

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

    override val onChange: StateFlow<StytchObjectInfo<B2BSessionData>> =
        combine(sessionStorage.sessionFlow, sessionStorage.lastValidatedAtFlow, ::stytchObjectMapper)
            .stateIn(
                externalScope,
                SharingStarted.WhileSubscribed(),
                stytchObjectMapper<B2BSessionData>(sessionStorage.memberSession, sessionStorage.lastValidatedAt),
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

    override fun onChange(callback: (StytchObjectInfo<B2BSessionData>) -> Unit) {
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

    override suspend fun authenticate(authParams: B2BSessions.AuthParams): SessionsAuthenticateResponse =
        withContext(dispatchers.io) {
            var result =
                api
                    .authenticate(
                        authParams.sessionDurationMinutes,
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
            if (result is StytchResult.Error) {
                result = maybeForceClearSession(result, false)
            }
            result
        }

    override fun authenticate(
        authParams: B2BSessions.AuthParams,
        callback: (SessionsAuthenticateResponse) -> Unit,
    ) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(authParams)
            // change to main thread to call callback
            callback(result)
        }
    }

    override fun authenticateCompletable(
        authParams: B2BSessions.AuthParams,
    ): CompletableFuture<SessionsAuthenticateResponse> =
        externalScope
            .async {
                authenticate(authParams)
            }.asCompletableFuture()

    override suspend fun revoke(params: B2BSessions.RevokeParams): BaseResponse =
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
        params: B2BSessions.RevokeParams,
        callback: (BaseResponse) -> Unit,
    ) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = revoke(params)
            // change to main thread to call callback
            callback(result)
        }
    }

    override fun revokeCompletable(params: B2BSessions.RevokeParams): CompletableFuture<BaseResponse> =
        externalScope
            .async {
                revoke(params)
            }.asCompletableFuture()

    /**
     * @throws StytchInternalError if failed to save data
     */
    override fun updateSession(
        sessionToken: String,
        sessionJwt: String,
    ) {
        try {
            sessionStorage.updateSession(sessionToken = sessionToken, sessionJwt = sessionJwt)
        } catch (ex: Exception) {
            throw StytchInternalError(ex)
        }
    }

    override fun getSync(): B2BSessionData? = sessionStorage.memberSession

    override suspend fun exchange(parameters: B2BSessions.ExchangeParameters): SessionExchangeResponse =
        withContext(dispatchers.io) {
            api
                .exchange(
                    organizationId = parameters.organizationId,
                    locale = parameters.locale,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun exchange(
        parameters: B2BSessions.ExchangeParameters,
        callback: (SessionExchangeResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = exchange(parameters)
            // change to main thread to call callback
            callback(result)
        }
    }

    override fun exchangeCompletable(
        parameters: B2BSessions.ExchangeParameters,
    ): CompletableFuture<SessionExchangeResponse> =
        externalScope
            .async {
                exchange(parameters)
            }.asCompletableFuture()
}

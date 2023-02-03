package com.stytch.sdk.consumer.sessions

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.BaseResponse
import com.stytch.sdk.consumer.LoginOrCreateUserByEmailResponse
import com.stytch.sdk.consumer.network.StytchApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SessionsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val api: StytchApi.Sessions,
) : Sessions {
    override val sessionToken: String?
        get() {
            try {
                return sessionStorage.sessionToken
            } catch (ex: Exception) {
                throw StytchExceptions.Critical(ex)
            }
        }

    override val sessionJwt: String?
        get() {
            try {
                return sessionStorage.sessionJwt
            } catch (ex: Exception) {
                throw StytchExceptions.Critical(ex)
            }
        }

    override suspend fun authenticate(authParams: Sessions.AuthParams): AuthResponse {
        val result: AuthResponse
        withContext(dispatchers.io) {
            // do not revoke session here since we using stored data to authenticate
            // call backend endpoint
            result = api.authenticate(
                authParams.sessionDurationMinutes
            ).apply {
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }
        return result
    }

    override fun authenticate(authParams: Sessions.AuthParams, callback: (AuthResponse) -> Unit) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(authParams)
            // change to main thread to call callback
            callback(result)
        }
    }

    override suspend fun revoke(): BaseResponse {
        var result: LoginOrCreateUserByEmailResponse
        withContext(dispatchers.io) {
            result = api.revoke()
        }
        // remove stored session
        try {
            sessionStorage.revoke()
        } catch (ex: Exception) {
            result = StytchResult.Error(StytchExceptions.Critical(ex))
        }
        return result
    }

    override fun revoke(callback: (BaseResponse) -> Unit) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = revoke()
            // change to main thread to call callback
            callback(result)
        }
    }

    /**
     * @throws StytchExceptions.Critical if failed to save data
     */
    override fun updateSession(sessionToken: String?, sessionJwt: String?) {
        try {
            sessionStorage.updateSession(sessionToken, sessionJwt)
        } catch (ex: Exception) {
            throw StytchExceptions.Critical(ex)
        }
    }
}

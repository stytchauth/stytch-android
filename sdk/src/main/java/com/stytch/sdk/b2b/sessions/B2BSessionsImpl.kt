package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class B2BSessionsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Sessions,
) : B2BSessions {
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

    override suspend fun authenticate(authParams: B2BSessions.AuthParams): AuthResponse {
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

    override fun authenticate(authParams: B2BSessions.AuthParams, callback: (AuthResponse) -> Unit) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(authParams)
            // change to main thread to call callback
            callback(result)
        }
    }

    override suspend fun revoke(params: B2BSessions.RevokeParams?): BaseResponse {
        var result: BaseResponse
        withContext(dispatchers.io) {
            result = api.revoke()
        }
        // remove stored session
        try {
            if (result is StytchResult.Success || params?.forceClear == true) {
                sessionStorage.revoke()
            }
        } catch (ex: Exception) {
            result = StytchResult.Error(StytchExceptions.Critical(ex))
        }
        return result
    }

    override fun revoke(params: B2BSessions.RevokeParams?, callback: (BaseResponse) -> Unit) {
        // call endpoint in IO thread
        externalScope.launch(dispatchers.ui) {
            val result = revoke(params)
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

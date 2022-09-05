package com.stytch.sessions

import com.stytch.sdk.AuthResponse
import com.stytch.sdk.BaseResponse
import com.stytch.sdk.LoginOrCreateUserByEmailResponse
import com.stytch.sdk.Sessions
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SessionsImpl internal constructor() : Sessions {

    override suspend fun authenticate(authParams: Sessions.AuthParams): AuthResponse {
        return catchExceptions {
            val result: AuthResponse
            withContext(StytchClient.ioDispatcher) {

                // do not revoke session here since we using stored data to authenticate

                // call backend endpoint
                result = StytchApi.Sessions.authenticate(
                    authParams.sessionDurationMinutes?.toInt()
                ).apply {
                    saveSession()
                    launchSessionUpdater()
                }
            }
            result
        }
    }

    override fun authenticate(authParams: Sessions.AuthParams, callback: (AuthResponse) -> Unit) {
//          call endpoint in IO thread
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(authParams)
//          change to main thread to call callback
            callback(result)
        }
    }

    override suspend fun revoke(): BaseResponse {
        return catchExceptions {
            val result: LoginOrCreateUserByEmailResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.Sessions.revoke()
            }
//            remove stored session
            StytchClient.sessionStorage.revoke()
            result
        }
    }

    override fun revoke(callback: (BaseResponse) -> Unit) {
//          call endpoint in IO thread
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = revoke()
//          change to main thread to call callback
            callback(result)
        }
    }

    override fun updateSession(sessionToken: String?, sessionJwt: String?) {
        StytchClient.sessionStorage.updateSession(sessionToken, sessionJwt)
    }

    //    TODO: rethink error handling
    private suspend fun <StytchResultType> catchExceptions(function: suspend () -> StytchResult<StytchResultType>): StytchResult<StytchResultType> {
        return try {
            function()
        } catch (stytchException: StytchExceptions) {
            when (stytchException) {
                StytchExceptions.NoCodeChallengeFound ->
                    StytchResult.Error(1, null)
            }
        } catch (otherException: Exception) {
            StytchResult.Error(1, null)
        }
    }
}
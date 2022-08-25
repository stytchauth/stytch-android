package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SessionsImpl internal constructor() : Sessions {

    override suspend fun authenticate(authParams: Sessions.AuthParams): BaseResponse {
        return catchExceptions {
//            update session storage
            StytchClient.sessionStorage.updateSession(authParams.sessionToken, authParams.sessionJwt)
//            call backend
            val result: LoginOrCreateUserByEmailResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.Sessions.authenticate(
                    authParams.sessionDurationMinutes.toInt(),
                    authParams.sessionToken,
                    authParams.sessionJwt
                )
            }
            result
        }
    }

    override fun authenticate(authParams: Sessions.AuthParams, callback: (BaseResponse) -> Unit) {
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
                result = StytchApi.Sessions.revoke(
                    StytchClient.sessionStorage.sessionToken,
                    StytchClient.sessionStorage.sessionJwt
                )
            }
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
package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor() : MagicLinks {

    override val email: MagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(parameters: MagicLinks.AuthParameters): BaseResponse {
        return catchExceptions {
            val result: BaseResponse
            withContext(StytchClient.ioDispatcher) {
                val (_, challengeCode) = StytchClient.storageHelper.getHashedCodeChallenge(false)
                result = StytchApi.MagicLinks.Email.authenticate(
                    parameters.token,
                    parameters.sessionDurationMinutes,
                    challengeCode
                )
            }
            result
        }
    }

    override fun authenticate(
        parameters: MagicLinks.AuthParameters,
        callback: (response: BaseResponse) -> Unit,
    ) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(parameters)
//              change to main thread to call callback
            callback(result)
        }
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

    private inner class EmailMagicLinksImpl : MagicLinks.EmailMagicLinks {

        override suspend fun loginOrCreate(parameters: MagicLinks.EmailMagicLinks.Parameters): LoginOrCreateUserByEmailResponse {
            return catchExceptions {
                val result: LoginOrCreateUserByEmailResponse

                withContext(StytchClient.ioDispatcher) {
                    val (challengeCodeMethod, challengeCode) = StytchClient.storageHelper.getHashedCodeChallenge(true)
                    result = StytchApi.MagicLinks.Email.loginOrCreateEmail(
                        email = parameters.email,
                        loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                        challengeCode,
                        challengeCodeMethod
                    )
                }

                result
            }
        }

        override fun loginOrCreate(
            parameters: MagicLinks.EmailMagicLinks.Parameters,
            callback: (response: LoginOrCreateUserByEmailResponse) -> Unit,
        ) {
//          call endpoint in IO thread
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
//              change to main thread to call callback
                callback(result)
            }
        }
    }
}
package com.stytch.sdk.magicLinks

import com.stytch.sdk.AuthResponse
import com.stytch.sdk.BaseResponse
import com.stytch.sdk.LoginOrCreateUserByEmailResponse
import com.stytch.sdk.PREFERENCES_CODE_VERIFIER
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.StytchDispatchers
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.sessions.SessionStorage
import com.stytch.sdk.sessions.launchSessionUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.MagicLinks.Email
) : MagicLinks {

    override val email: MagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(parameters: MagicLinks.AuthParameters): AuthResponse {
        var result: AuthResponse
        withContext(dispatchers.io) {
            val codeVerifier: String

            try {
                codeVerifier = storageHelper.loadValue(PREFERENCES_CODE_VERIFIER)!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchExceptions.Critical(ex))
                return@withContext
            }

            // call backend endpoint
            result = api.authenticate(
                parameters.token,
                parameters.sessionDurationMinutes,
                codeVerifier
            ).apply {
                launchSessionUpdater(dispatchers, sessionStorage)
            }
        }

        return result
    }

    override fun authenticate(
        parameters: MagicLinks.AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            // change to main thread to call callback
            callback(result)
        }
    }

    private inner class EmailMagicLinksImpl : MagicLinks.EmailMagicLinks {

        override suspend fun loginOrCreate(
            parameters: MagicLinks.EmailMagicLinks.LoginOrCreateParameters
        ): LoginOrCreateUserByEmailResponse {
            val result: LoginOrCreateUserByEmailResponse
            withContext(dispatchers.io) {
                val challengeCodeMethod: String
                val challengeCode: String

                try {
                    val challengePair = storageHelper.generateHashedCodeChallenge()
                    challengeCodeMethod = challengePair.first
                    challengeCode = challengePair.second
                } catch (ex: Exception) {
                    result = StytchResult.Error(StytchExceptions.Critical(ex))
                    return@withContext
                }

                result = api.loginOrCreate(
                    email = parameters.email,
                    loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                    codeChallenge = challengeCode,
                    codeChallengeMethod = challengeCodeMethod,
                    loginTemplateId = parameters.loginTemplateId,
                    signupTemplateId = parameters.signupTemplateId,
                )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: MagicLinks.EmailMagicLinks.LoginOrCreateParameters,
            callback: (response: LoginOrCreateUserByEmailResponse) -> Unit,
        ) {
            // call endpoint in IO thread
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                // change to main thread to call callback
                callback(result)
            }
        }

        override suspend fun send(parameters: MagicLinks.EmailMagicLinks.SendParameters): BaseResponse =
            withContext(dispatchers.io) {
                val challengeCode: String
                try {
                    val challengePair = storageHelper.generateHashedCodeChallenge()
                    challengeCode = challengePair.second
                } catch (ex: Exception) {
                    return@withContext StytchResult.Error(StytchExceptions.Critical(ex))
                }
                api.send(
                    email = parameters.email,
                    loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                    signupMagicLinkUrl = parameters.signupMagicLinkUrl,
                    loginExpirationMinutes = parameters.loginExpirationMinutes?.toInt(),
                    signupExpirationMinutes = parameters.signupExpirationMinutes?.toInt(),
                    loginTemplateId = parameters.loginTemplateId,
                    signupTemplateId = parameters.signupTemplateId,
                    codeChallenge = challengeCode,
                )
            }

        override fun send(
            parameters: MagicLinks.EmailMagicLinks.SendParameters,
            callback: (response: BaseResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }
}

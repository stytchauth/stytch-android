package com.stytch.sdk.consumer.magicLinks

import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MagicLinksImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val storageHelper: StorageHelper,
    private val api: StytchApi.MagicLinks.Email
) : MagicLinks {

    override val email: MagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(parameters: MagicLinks.AuthParameters): AuthResponse {
        var result: AuthResponse
        withContext(dispatchers.io) {
            val codeVerifier: String

            try {
                codeVerifier = storageHelper.retrieveCodeVerifier()!!
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
            parameters: MagicLinks.EmailMagicLinks.Parameters
        ): BaseResponse {
            val result: BaseResponse
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
            parameters: MagicLinks.EmailMagicLinks.Parameters,
            callback: (response: BaseResponse) -> Unit,
        ) {
            // call endpoint in IO thread
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                // change to main thread to call callback
                callback(result)
            }
        }

        override suspend fun send(parameters: MagicLinks.EmailMagicLinks.Parameters): BaseResponse =
            withContext(dispatchers.io) {
                val challengeCode: String
                try {
                    val challengePair = storageHelper.generateHashedCodeChallenge()
                    challengeCode = challengePair.second
                } catch (ex: Exception) {
                    return@withContext StytchResult.Error(StytchExceptions.Critical(ex))
                }
                if (sessionStorage.persistedSessionIdentifiersExist) {
                    api.sendSecondary(
                        email = parameters.email,
                        loginMagicLinkUrl = parameters.loginMagicLinkUrl,
                        signupMagicLinkUrl = parameters.signupMagicLinkUrl,
                        loginExpirationMinutes = parameters.loginExpirationMinutes?.toInt(),
                        signupExpirationMinutes = parameters.signupExpirationMinutes?.toInt(),
                        loginTemplateId = parameters.loginTemplateId,
                        signupTemplateId = parameters.signupTemplateId,
                        codeChallenge = challengeCode,
                    )
                } else {
                    api.sendPrimary(
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
            }

        override fun send(
            parameters: MagicLinks.EmailMagicLinks.Parameters,
            callback: (response: BaseResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }
}

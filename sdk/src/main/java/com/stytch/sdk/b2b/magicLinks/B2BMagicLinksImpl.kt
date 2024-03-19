package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.AuthResponse
import com.stytch.sdk.b2b.DiscoveryEMLAuthResponse
import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchFailedToCreateCodeChallengeError
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class B2BMagicLinksImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val storageHelper: StorageHelper,
    private val emailApi: StytchB2BApi.MagicLinks.Email,
    private val discoveryApi: StytchB2BApi.MagicLinks.Discovery,
) : B2BMagicLinks {
    override val email: B2BMagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(parameters: B2BMagicLinks.AuthParameters): AuthResponse {
        var result: AuthResponse
        withContext(dispatchers.io) {
            val codeVerifier: String

            try {
                codeVerifier = storageHelper.retrieveCodeVerifier()!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchMissingPKCEError(ex))
                return@withContext
            }

            // call backend endpoint
            result =
                emailApi.authenticate(
                    parameters.token,
                    parameters.sessionDurationMinutes,
                    codeVerifier,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

        return result
    }

    override fun authenticate(
        parameters: B2BMagicLinks.AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            // change to main thread to call callback
            callback(result)
        }
    }

    override suspend fun discoveryAuthenticate(
        parameters: B2BMagicLinks.DiscoveryAuthenticateParameters,
    ): DiscoveryEMLAuthResponse {
        var result: DiscoveryEMLAuthResponse
        withContext(dispatchers.io) {
            val codeVerifier: String
            try {
                codeVerifier = storageHelper.retrieveCodeVerifier()!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchMissingPKCEError(ex))
                return@withContext
            }
            result =
                discoveryApi.authenticate(
                    token = parameters.token,
                    codeVerifier = codeVerifier,
                )
        }
        return result
    }

    override fun discoveryAuthenticate(
        parameters: B2BMagicLinks.DiscoveryAuthenticateParameters,
        callback: (DiscoveryEMLAuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = discoveryAuthenticate(parameters)
            callback(result)
        }
    }

    private inner class EmailMagicLinksImpl : B2BMagicLinks.EmailMagicLinks {
        override suspend fun loginOrSignup(parameters: B2BMagicLinks.EmailMagicLinks.Parameters): BaseResponse {
            val result: BaseResponse
            withContext(dispatchers.io) {
                val challengeCode: String
                try {
                    val challengePair = storageHelper.generateHashedCodeChallenge()
                    challengeCode = challengePair.second
                } catch (ex: Exception) {
                    result = StytchResult.Error(StytchFailedToCreateCodeChallengeError(exception = ex))
                    return@withContext
                }

                result =
                    emailApi.loginOrSignupByEmail(
                        email = parameters.email,
                        organizationId = parameters.organizationId,
                        loginRedirectUrl = parameters.loginRedirectUrl,
                        signupRedirectUrl = parameters.signupRedirectUrl,
                        codeChallenge = challengeCode,
                        loginTemplateId = parameters.loginTemplateId,
                        signupTemplateId = parameters.signupTemplateId,
                    )
            }

            return result
        }

        override fun loginOrSignup(
            parameters: B2BMagicLinks.EmailMagicLinks.Parameters,
            callback: (response: BaseResponse) -> Unit,
        ) {
            // call endpoint in IO thread
            externalScope.launch(dispatchers.ui) {
                val result = loginOrSignup(parameters)
                // change to main thread to call callback
                callback(result)
            }
        }

        override suspend fun discoverySend(
            parameters: B2BMagicLinks.EmailMagicLinks.DiscoverySendParameters,
        ): BaseResponse {
            val result: BaseResponse
            withContext(dispatchers.io) {
                val challengeCode: String
                try {
                    val challengePair = storageHelper.generateHashedCodeChallenge()
                    challengeCode = challengePair.second
                } catch (ex: Exception) {
                    result = StytchResult.Error(StytchFailedToCreateCodeChallengeError(exception = ex))
                    return@withContext
                }

                result =
                    discoveryApi.send(
                        email = parameters.emailAddress,
                        codeChallenge = challengeCode,
                        loginTemplateId = parameters.loginTemplateId,
                        discoveryRedirectUrl = parameters.discoveryRedirectUrl,
                    )
            }
            return result
        }

        override fun discoverySend(
            parameters: B2BMagicLinks.EmailMagicLinks.DiscoverySendParameters,
            callback: (BaseResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = discoverySend(parameters)
                callback(result)
            }
        }

        override suspend fun invite(parameters: B2BMagicLinks.EmailMagicLinks.InviteParameters): MemberResponse {
            return withContext(dispatchers.io) {
                emailApi.invite(
                    emailAddress = parameters.emailAddress,
                    inviteRedirectUrl = parameters.inviteRedirectUrl,
                    inviteTemplateId = parameters.inviteTemplateId,
                    name = parameters.name,
                    untrustedMetadata = parameters.untrustedMetadata,
                    locale = parameters.locale,
                    roles = parameters.roles,
                )
            }
        }

        override fun invite(
            parameters: B2BMagicLinks.EmailMagicLinks.InviteParameters,
            callback: (MemberResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = invite(parameters)
                callback(result)
            }
        }
    }
}

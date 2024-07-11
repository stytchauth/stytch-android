package com.stytch.sdk.b2b.magicLinks

import com.stytch.sdk.b2b.DiscoveryEMLAuthResponse
import com.stytch.sdk.b2b.EMLAuthenticateResponse
import com.stytch.sdk.b2b.MemberResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.BaseResponse
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchFailedToCreateCodeChallengeError
import com.stytch.sdk.common.errors.StytchMissingPKCEError
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class B2BMagicLinksImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val emailApi: StytchB2BApi.MagicLinks.Email,
    private val discoveryApi: StytchB2BApi.MagicLinks.Discovery,
    private val pkcePairManager: PKCEPairManager,
) : B2BMagicLinks {
    override val email: B2BMagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(parameters: B2BMagicLinks.AuthParameters): EMLAuthenticateResponse {
        var result: EMLAuthenticateResponse
        withContext(dispatchers.io) {
            result =
                emailApi.authenticate(
                    token = parameters.token,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    codeVerifier = pkcePairManager.getPKCECodePair()?.codeVerifier,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            pkcePairManager.clearPKCECodePair()
        }

        return result
    }

    override fun authenticate(
        parameters: B2BMagicLinks.AuthParameters,
        callback: (response: EMLAuthenticateResponse) -> Unit,
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
                codeVerifier = pkcePairManager.getPKCECodePair()?.codeVerifier!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchMissingPKCEError(ex))
                return@withContext
            }
            result =
                discoveryApi.authenticate(
                    token = parameters.token,
                    codeVerifier = codeVerifier,
                )
            pkcePairManager.clearPKCECodePair()
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
                    challengeCode = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
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
                    challengeCode = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
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

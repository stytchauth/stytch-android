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
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class B2BMagicLinksImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val emailApi: StytchB2BApi.MagicLinks.Email,
    private val discoveryApi: StytchB2BApi.MagicLinks.Discovery,
    private val pkcePairManager: PKCEPairManager,
) : B2BMagicLinks {
    override val email: B2BMagicLinks.EmailMagicLinks = EmailMagicLinksImpl()

    override suspend fun authenticate(parameters: B2BMagicLinks.AuthParameters): EMLAuthenticateResponse =
        withContext(dispatchers.io) {
            emailApi
                .authenticate(
                    token = parameters.token,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    codeVerifier = pkcePairManager.getPKCECodePair()?.codeVerifier,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                ).apply {
                    pkcePairManager.clearPKCECodePair()
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
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

    override fun authenticateCompletable(
        parameters: B2BMagicLinks.AuthParameters,
    ): CompletableFuture<EMLAuthenticateResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()

    override suspend fun discoveryAuthenticate(
        parameters: B2BMagicLinks.DiscoveryAuthenticateParameters,
    ): DiscoveryEMLAuthResponse {
        val codeVerifier =
            pkcePairManager.getPKCECodePair()?.codeVerifier ?: return StytchResult.Error(StytchMissingPKCEError(null))
        return withContext(dispatchers.io) {
            discoveryApi
                .authenticate(
                    token = parameters.token,
                    codeVerifier = codeVerifier,
                ).also {
                    pkcePairManager.clearPKCECodePair()
                }
        }
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

    override fun discoveryAuthenticateCompletable(
        parameters: B2BMagicLinks.DiscoveryAuthenticateParameters,
    ): CompletableFuture<DiscoveryEMLAuthResponse> =
        externalScope
            .async {
                discoveryAuthenticate(parameters)
            }.asCompletableFuture()

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
                        locale = parameters.locale,
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

        override fun loginOrSignupCompletable(
            parameters: B2BMagicLinks.EmailMagicLinks.Parameters,
        ): CompletableFuture<BaseResponse> =
            externalScope
                .async {
                    loginOrSignup(parameters)
                }.asCompletableFuture()

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
                        locale = parameters.locale,
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

        override fun discoverySendCompletable(
            parameters: B2BMagicLinks.EmailMagicLinks.DiscoverySendParameters,
        ): CompletableFuture<BaseResponse> =
            externalScope
                .async {
                    discoverySend(parameters)
                }.asCompletableFuture()

        override suspend fun invite(parameters: B2BMagicLinks.EmailMagicLinks.InviteParameters): MemberResponse =
            withContext(dispatchers.io) {
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

        override fun invite(
            parameters: B2BMagicLinks.EmailMagicLinks.InviteParameters,
            callback: (MemberResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = invite(parameters)
                callback(result)
            }
        }

        override fun inviteCompletable(
            parameters: B2BMagicLinks.EmailMagicLinks.InviteParameters,
        ): CompletableFuture<MemberResponse> =
            externalScope
                .async {
                    invite(parameters)
                }.asCompletableFuture()
    }
}

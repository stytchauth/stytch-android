package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.b2b.B2BPasswordDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.B2BPasswordDiscoveryResetByEmailResponse
import com.stytch.sdk.b2b.EmailResetResponse
import com.stytch.sdk.b2b.PasswordResetByExistingPasswordResponse
import com.stytch.sdk.b2b.PasswordStrengthCheckResponse
import com.stytch.sdk.b2b.PasswordsAuthenticateResponse
import com.stytch.sdk.b2b.SessionResetResponse
import com.stytch.sdk.b2b.discovery.DiscoveryImpl
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

@Suppress("TooManyFunctions")
internal class PasswordsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Passwords,
    private val discoveryApi: StytchB2BApi.Passwords.Discovery,
    private val pkcePairManager: PKCEPairManager,
) : Passwords {
    override suspend fun authenticate(parameters: Passwords.AuthParameters): PasswordsAuthenticateResponse =
        withContext(dispatchers.io) {
            api
                .authenticate(
                    organizationId = parameters.organizationId,
                    emailAddress = parameters.emailAddress,
                    password = parameters.password,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                    locale = parameters.locale,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun authenticate(
        parameters: Passwords.AuthParameters,
        callback: (PasswordsAuthenticateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    override fun authenticateCompletable(
        parameters: Passwords.AuthParameters,
    ): CompletableFuture<PasswordsAuthenticateResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()

    override suspend fun resetByEmailStart(parameters: Passwords.ResetByEmailStartParameters): BaseResponse {
        val result: BaseResponse
        withContext(dispatchers.io) {
            val challengeCode: String
            try {
                challengeCode = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchFailedToCreateCodeChallengeError(ex))
                return@withContext
            }
            result =
                api.resetByEmailStart(
                    organizationId = parameters.organizationId,
                    emailAddress = parameters.emailAddress,
                    loginRedirectUrl = parameters.loginRedirectUrl,
                    resetPasswordRedirectUrl = parameters.resetPasswordRedirectUrl,
                    resetPasswordExpirationMinutes = parameters.resetPasswordExpirationMinutes,
                    resetPasswordTemplateId = parameters.resetPasswordTemplateId,
                    codeChallenge = challengeCode,
                )
        }
        return result
    }

    override fun resetByEmailStart(
        parameters: Passwords.ResetByEmailStartParameters,
        callback: (BaseResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetByEmailStart(parameters)
            callback(result)
        }
    }

    override fun resetByEmailStartCompletable(
        parameters: Passwords.ResetByEmailStartParameters,
    ): CompletableFuture<BaseResponse> =
        externalScope
            .async {
                resetByEmailStart(parameters)
            }.asCompletableFuture()

    override suspend fun resetByEmail(parameters: Passwords.ResetByEmailParameters): EmailResetResponse {
        val codeVerifier =
            pkcePairManager.getPKCECodePair()?.codeVerifier ?: return StytchResult.Error(StytchMissingPKCEError(null))
        return withContext(dispatchers.io) {
            api
                .resetByEmail(
                    passwordResetToken = parameters.token,
                    password = parameters.password,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    codeVerifier = codeVerifier,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                    locale = parameters.locale,
                ).apply {
                    pkcePairManager.clearPKCECodePair()
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }
    }

    override fun resetByEmail(
        parameters: Passwords.ResetByEmailParameters,
        callback: (EmailResetResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetByEmail(parameters)
            callback(result)
        }
    }

    override fun resetByEmailCompletable(
        parameters: Passwords.ResetByEmailParameters,
    ): CompletableFuture<EmailResetResponse> =
        externalScope
            .async {
                resetByEmail(parameters)
            }.asCompletableFuture()

    override suspend fun resetByExisting(
        parameters: Passwords.ResetByExistingPasswordParameters,
    ): PasswordResetByExistingPasswordResponse =
        withContext(dispatchers.io) {
            api
                .resetByExisting(
                    organizationId = parameters.organizationId,
                    emailAddress = parameters.emailAddress,
                    existingPassword = parameters.existingPassword,
                    newPassword = parameters.newPassword,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    locale = parameters.locale,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun resetByExisting(
        parameters: Passwords.ResetByExistingPasswordParameters,
        callback: (PasswordResetByExistingPasswordResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetByExisting(parameters)
            callback(result)
        }
    }

    override fun resetByExistingCompletable(
        parameters: Passwords.ResetByExistingPasswordParameters,
    ): CompletableFuture<PasswordResetByExistingPasswordResponse> =
        externalScope
            .async {
                resetByExisting(parameters)
            }.asCompletableFuture()

    override suspend fun resetBySession(parameters: Passwords.ResetBySessionParameters): SessionResetResponse =
        withContext(dispatchers.io) {
            api.resetBySession(
                organizationId = parameters.organizationId,
                password = parameters.password,
            )
        }

    override fun resetBySession(
        parameters: Passwords.ResetBySessionParameters,
        callback: (SessionResetResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = resetBySession(parameters)
            callback(result)
        }
    }

    override fun resetBySessionCompletable(
        parameters: Passwords.ResetBySessionParameters,
    ): CompletableFuture<SessionResetResponse> =
        externalScope
            .async {
                resetBySession(parameters)
            }.asCompletableFuture()

    override suspend fun strengthCheck(parameters: Passwords.StrengthCheckParameters): PasswordStrengthCheckResponse =
        withContext(dispatchers.io) {
            api.strengthCheck(
                email = parameters.email,
                password = parameters.password,
            )
        }

    override fun strengthCheck(
        parameters: Passwords.StrengthCheckParameters,
        callback: (PasswordStrengthCheckResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = strengthCheck(parameters)
            callback(result)
        }
    }

    override fun strengthCheckCompletable(
        parameters: Passwords.StrengthCheckParameters,
    ): CompletableFuture<PasswordStrengthCheckResponse> =
        externalScope
            .async {
                strengthCheck(parameters)
            }.asCompletableFuture()

    override val discovery: Passwords.Discovery = DiscoveryImpl()

    private inner class DiscoveryImpl : Passwords.Discovery {
        override suspend fun resetByEmailStart(
            parameters: Passwords.Discovery.ResetByEmailStartParameters,
        ): BaseResponse =
            withContext(dispatchers.io) {
                val challengeCode: String
                try {
                    challengeCode = pkcePairManager.generateAndReturnPKCECodePair().codeChallenge
                } catch (ex: Exception) {
                    return@withContext StytchResult.Error(StytchFailedToCreateCodeChallengeError(exception = ex))
                }
                discoveryApi.resetByEmailStart(
                    emailAddress = parameters.emailAddress,
                    discoveryRedirectUrl = parameters.discoveryRedirectUrl,
                    resetPasswordRedirectUrl = parameters.resetPasswordRedirectUrl,
                    resetPasswordExpirationMinutes = parameters.resetPasswordExpirationMinutes,
                    resetPasswordTemplateId = parameters.resetPasswordTemplateId,
                    codeChallenge = challengeCode,
                )
            }

        override fun resetByEmailStart(
            parameters: Passwords.Discovery.ResetByEmailStartParameters,
            callback: (BaseResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(resetByEmailStart(parameters))
            }
        }

        override fun resetByEmailStartCompletable(
            parameters: Passwords.Discovery.ResetByEmailStartParameters,
        ): CompletableFuture<BaseResponse> =
            externalScope
                .async { resetByEmailStart(parameters) }
                .asCompletableFuture()

        override suspend fun resetByEmail(
            parameters: Passwords.Discovery.ResetByEmailParameters,
        ): B2BPasswordDiscoveryResetByEmailResponse =
            withContext(dispatchers.io) {
                discoveryApi.resetByEmail(
                    passwordResetToken = parameters.passwordResetToken,
                    password = parameters.password,
                    codeVerifier = pkcePairManager.getPKCECodePair()?.codeVerifier,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                )
            }.apply {
                pkcePairManager.clearPKCECodePair()
                if (this is StytchResult.Success) {
                    sessionStorage.intermediateSessionToken = value.intermediateSessionToken
                }
            }

        override fun resetByEmail(
            parameters: Passwords.Discovery.ResetByEmailParameters,
            callback: (B2BPasswordDiscoveryResetByEmailResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(resetByEmail(parameters))
            }
        }

        override fun resetByEmailCompletable(
            parameters: Passwords.Discovery.ResetByEmailParameters,
        ): CompletableFuture<B2BPasswordDiscoveryResetByEmailResponse> =
            externalScope
                .async { resetByEmail(parameters) }
                .asCompletableFuture()

        override suspend fun authenticate(
            parameters: Passwords.Discovery.AuthenticateParameters,
        ): B2BPasswordDiscoveryAuthenticateResponse =
            withContext(dispatchers.io) {
                discoveryApi.authenticate(
                    emailAddress = parameters.emailAddress,
                    password = parameters.password,
                )
            }.apply {
                if (this is StytchResult.Success) {
                    sessionStorage.intermediateSessionToken = value.intermediateSessionToken
                }
            }

        override fun authenticate(
            parameters: Passwords.Discovery.AuthenticateParameters,
            callback: (B2BPasswordDiscoveryAuthenticateResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(authenticate(parameters))
            }
        }

        override fun authenticateCompletable(
            parameters: Passwords.Discovery.AuthenticateParameters,
        ): CompletableFuture<B2BPasswordDiscoveryAuthenticateResponse> =
            externalScope
                .async { authenticate(parameters) }
                .asCompletableFuture()
    }
}

package com.stytch.sdk.b2b.passwords

import com.stytch.sdk.b2b.EmailResetResponse
import com.stytch.sdk.b2b.PasswordResetByExistingPasswordResponse
import com.stytch.sdk.b2b.PasswordStrengthCheckResponse
import com.stytch.sdk.b2b.PasswordsAuthenticateResponse
import com.stytch.sdk.b2b.SessionResetResponse
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

@Suppress("TooManyFunctions")
internal class PasswordsImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.Passwords,
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

    override suspend fun resetByEmail(parameters: Passwords.ResetByEmailParameters): EmailResetResponse {
        val result: EmailResetResponse
        withContext(dispatchers.io) {
            val codeVerifier: String
            try {
                codeVerifier = pkcePairManager.getPKCECodePair()?.codeVerifier!!
            } catch (ex: Exception) {
                result = StytchResult.Error(StytchMissingPKCEError(ex))
                return@withContext
            }
            result =
                api
                    .resetByEmail(
                        passwordResetToken = parameters.token,
                        password = parameters.password,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                        codeVerifier = codeVerifier,
                        intermediateSessionToken = sessionStorage.intermediateSessionToken,
                        locale = parameters.locale,
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
            pkcePairManager.clearPKCECodePair()
        }
        return result
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
}

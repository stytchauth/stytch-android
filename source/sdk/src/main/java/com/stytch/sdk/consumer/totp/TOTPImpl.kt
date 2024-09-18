package com.stytch.sdk.consumer.totp

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.consumer.TOTPAuthenticateResponse
import com.stytch.sdk.consumer.TOTPCreateResponse
import com.stytch.sdk.consumer.TOTPRecoverResponse
import com.stytch.sdk.consumer.TOTPRecoveryCodesResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class TOTPImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.TOTP,
) : TOTP {
    override suspend fun create(parameters: TOTP.CreateParameters): TOTPCreateResponse =
        withContext(dispatchers.io) {
            api.create(
                expirationMinutes = parameters.expirationMinutes,
            )
        }

    override fun create(
        parameters: TOTP.CreateParameters,
        callback: (TOTPCreateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(create(parameters))
        }
    }

    override fun createCompletable(parameters: TOTP.CreateParameters): CompletableFuture<TOTPCreateResponse> =
        externalScope
            .async {
                create(parameters)
            }.asCompletableFuture()

    override suspend fun authenticate(parameters: TOTP.AuthenticateParameters): TOTPAuthenticateResponse =
        withContext(dispatchers.io) {
            api
                .authenticate(
                    totpCode = parameters.totpCode,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun authenticate(
        parameters: TOTP.AuthenticateParameters,
        callback: (TOTPAuthenticateResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(authenticate(parameters))
        }
    }

    override fun authenticateCompletable(
        parameters: TOTP.AuthenticateParameters,
    ): CompletableFuture<TOTPAuthenticateResponse> =
        externalScope
            .async {
                authenticate(parameters)
            }.asCompletableFuture()

    override suspend fun recoveryCodes(): TOTPRecoveryCodesResponse =
        withContext(dispatchers.io) {
            api.recoveryCodes()
        }

    override fun recoveryCodes(callback: (TOTPRecoveryCodesResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(recoveryCodes())
        }
    }

    override fun recoveryCodesCompletable(): CompletableFuture<TOTPRecoveryCodesResponse> =
        externalScope
            .async {
                recoveryCodes()
            }.asCompletableFuture()

    override suspend fun recover(parameters: TOTP.RecoverParameters): TOTPRecoverResponse =
        withContext(dispatchers.io) {
            api
                .recover(
                    recoveryCode = parameters.recoveryCode,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
        }

    override fun recover(
        parameters: TOTP.RecoverParameters,
        callback: (TOTPRecoverResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(recover(parameters))
        }
    }

    override fun recoverCompletable(parameters: TOTP.RecoverParameters): CompletableFuture<TOTPRecoverResponse> =
        externalScope
            .async {
                recover(parameters)
            }.asCompletableFuture()
}

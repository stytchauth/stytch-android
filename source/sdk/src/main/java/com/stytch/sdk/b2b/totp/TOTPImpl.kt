package com.stytch.sdk.b2b.totp

import com.stytch.sdk.b2b.TOTPAuthenticateResponse
import com.stytch.sdk.b2b.TOTPCreateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class TOTPImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.TOTP,
) : TOTP {
    override suspend fun create(parameters: TOTP.CreateParameters): TOTPCreateResponse =
        withContext(dispatchers.io) {
            api.create(
                organizationId = parameters.organizationId,
                memberId = parameters.memberId,
                expirationMinutes = parameters.expirationMinutes,
                intermediateSessionToken = sessionStorage.intermediateSessionToken,
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
                    organizationId = parameters.organizationId,
                    memberId = parameters.memberId,
                    code = parameters.code,
                    setMFAEnrollment = parameters.setMFAEnrollment,
                    setDefaultMfaMethod = parameters.setDefaultMFAMethod,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
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
}

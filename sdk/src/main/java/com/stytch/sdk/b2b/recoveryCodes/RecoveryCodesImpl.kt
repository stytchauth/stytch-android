package com.stytch.sdk.b2b.recoveryCodes

import com.stytch.sdk.b2b.RecoveryCodesGetResponse
import com.stytch.sdk.b2b.RecoveryCodesRecoverResponse
import com.stytch.sdk.b2b.RecoveryCodesRotateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class RecoveryCodesImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.RecoveryCodes,
) : RecoveryCodes {
    override suspend fun get(): RecoveryCodesGetResponse =
        withContext(dispatchers.io) {
            api.get()
        }

    override fun get(callback: (RecoveryCodesGetResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(get())
        }
    }

    override suspend fun rotate(): RecoveryCodesRotateResponse =
        withContext(dispatchers.io) {
            api.rotate()
        }

    override fun rotate(callback: (RecoveryCodesRotateResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            callback(rotate())
        }
    }

    override suspend fun recover(parameters: RecoveryCodes.RecoverParameters): RecoveryCodesRecoverResponse =
        withContext(dispatchers.io) {
            api.recover(
                organizationId = parameters.organizationId,
                memberId = parameters.memberId,
                sessionDurationMinutes = parameters.sessionDurationMinutes,
                recoveryCode = parameters.recoveryCode,
                intermediateSessionToken = sessionStorage.intermediateSessionToken,
            ).apply {
                if (this is StytchResult.Success) {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            }
        }

    override fun recover(
        parameters: RecoveryCodes.RecoverParameters,
        callback: (RecoveryCodesRecoverResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            callback(recover(parameters))
        }
    }
}

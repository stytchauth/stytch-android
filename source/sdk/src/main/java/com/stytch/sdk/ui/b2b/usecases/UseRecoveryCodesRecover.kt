package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.RecoveryCodeRecoverResponseData
import com.stytch.sdk.b2b.recoveryCodes.RecoveryCodes
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseRecoveryCodesRecover(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<RecoveryCodeRecoverResponseData>,
) {
    operator fun invoke(recoveryCode: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.recoveryCodes.recover(
                    RecoveryCodes.RecoverParameters(
                        organizationId = state.value.mfaPrimaryInfoState?.organizationId ?: "",
                        memberId = state.value.mfaPrimaryInfoState?.memberId ?: "",
                        recoveryCode = recoveryCode,
                        sessionDurationMinutes = productConfig.sessionOptions.sessionDurationMinutes,
                    ),
                )
            }
        }
    }
}

package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.TOTPAuthenticateResponseData
import com.stytch.sdk.b2b.totp.TOTP
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseTOTPAuthenticate(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<TOTPAuthenticateResponseData>,
) {
    operator fun invoke(code: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.totp.authenticate(
                    TOTP.AuthenticateParameters(
                        organizationId = state.value.mfaPrimaryInfoState?.organizationId ?: "",
                        memberId = state.value.mfaPrimaryInfoState?.memberId ?: "",
                        code = code,
                        sessionDurationMinutes = productConfig.sessionOptions.sessionDurationMinutes,
                    ),
                )
            }
        }
    }
}

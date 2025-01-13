package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.SMSAuthenticateResponseData
import com.stytch.sdk.b2b.otp.OTP
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseOTPSMSAuthenticate(
    private val scope: CoroutineScope,
    private val productConfig: StytchB2BProductConfig,
    private val state: StateFlow<B2BUIState>,
    private val request: PerformRequest<SMSAuthenticateResponseData>,
) {
    operator fun invoke(code: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.otp.sms.authenticate(
                    OTP.SMS.AuthenticateParameters(
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

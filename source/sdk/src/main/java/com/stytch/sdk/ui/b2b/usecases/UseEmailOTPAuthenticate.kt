package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BOTPsEmailAuthenticateResponseData
import com.stytch.sdk.b2b.otp.OTP
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.flow.StateFlow

internal class UseEmailOTPAuthenticate(
    private val productConfig: StytchB2BProductConfig,
    private val state: StateFlow<B2BUIState>,
    private val request: PerformRequest<B2BOTPsEmailAuthenticateResponseData>,
) {
    suspend operator fun invoke(code: String) =
        request {
            StytchB2BClient.otp.email.authenticate(
                OTP.Email.AuthenticateParameters(
                    code = code,
                    organizationId = state.value.mfaPrimaryInfoState?.organizationId ?: "",
                    emailAddress = state.value.emailState.emailAddress,
                    sessionDurationMinutes = productConfig.sessionOptions.sessionDurationMinutes,
                ),
            )
        }
}

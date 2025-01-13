package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BDiscoveryOTPEmailAuthenticateResponseData
import com.stytch.sdk.b2b.otp.OTP
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import kotlinx.coroutines.flow.StateFlow

internal class UseEmailOTPDiscoveryAuthenticate(
    private val state: StateFlow<B2BUIState>,
    private val request: PerformRequest<B2BDiscoveryOTPEmailAuthenticateResponseData>,
) {
    suspend operator fun invoke(code: String) =
        request {
            StytchB2BClient.otp.email.discovery.authenticate(
                OTP.Email.Discovery.AuthenticateParameters(
                    code = code,
                    emailAddress = state.value.emailState.emailAddress,
                ),
            )
        }
}

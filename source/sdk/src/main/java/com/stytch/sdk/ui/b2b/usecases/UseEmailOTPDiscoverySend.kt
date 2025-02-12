package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BDiscoveryOTPEmailSendResponseData
import com.stytch.sdk.b2b.otp.OTP
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseEmailOTPDiscoverySend(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<B2BDiscoveryOTPEmailSendResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.otp.email.discovery.send(
                    OTP.Email.Discovery.SendParameters(
                        emailAddress = state.value.emailState.emailAddress,
                        loginTemplateId = productConfig.emailMagicLinksOptions.loginTemplateId,
                        locale = productConfig.locale,
                    ),
                )
            }.onSuccess {
                dispatch(SetNextRoute(Routes.EmailOTPEntry))
            }
        }
    }
}

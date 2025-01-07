package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BOTPsEmailLoginOrSignupResponseData
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

internal class UseEmailOTPLoginOrSignup(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<B2BOTPsEmailLoginOrSignupResponseData>,
) {
    operator fun invoke() {
        val orgId =
            state.value.mfaPrimaryInfoState?.organizationId
                ?: state.value.activeOrganization?.organizationId
                ?: return
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.otp.email.loginOrSignup(
                    OTP.Email.LoginOrSignupParameters(
                        emailAddress = state.value.emailState.emailAddress,
                        organizationId = orgId,
                        loginTemplateId = productConfig.emailMagicLinksOptions.loginTemplateId,
                        signupTemplateId = productConfig.emailMagicLinksOptions.signupTemplateId,
                    ),
                )
            }.onSuccess {
                dispatch(SetNextRoute(Routes.EmailOTPEntry))
            }
        }
    }
}

package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.otp.OTP
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.MFASMSState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.SetPostAuthScreen
import com.stytch.sdk.ui.b2b.data.UpdateMfaSmsState
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseOTPSMSSend(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val request: PerformRequest<BasicData>,
) {
    operator fun invoke(isEnrolling: Boolean) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.otp.sms.send(
                    OTP.SMS.SendParameters(
                        organizationId = state.value.mfaPrimaryInfoState?.organizationId ?: "",
                        memberId = state.value.mfaPrimaryInfoState?.memberId ?: "",
                        mfaPhoneNumber = state.value.phoneNumberState.toE164(),
                    ),
                )
            }.onSuccess {
                dispatch(
                    UpdateMfaSmsState(
                        state.value.mfaSMSState?.copy(isEnrolling = isEnrolling, didSend = true)
                            ?: MFASMSState(isEnrolling = isEnrolling, didSend = true),
                    ),
                )
                if (isEnrolling) {
                    // It's possible that we created (but didn't verify) TOTP, now that we added back buttons, so make
                    // sure that we reset the expected post auth screen
                    dispatch(SetPostAuthScreen(Routes.Success))
                }
                dispatch(SetNextRoute(Routes.SMSOTPEntry))
            }
        }
    }
}

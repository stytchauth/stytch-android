package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.TOTPCreateResponseData
import com.stytch.sdk.b2b.totp.TOTP
import com.stytch.sdk.common.extensions.toStytchError
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.MFATOTPState
import com.stytch.sdk.ui.b2b.data.SetPostAuthScreen
import com.stytch.sdk.ui.b2b.data.TOTPEnrollmentState
import com.stytch.sdk.ui.b2b.data.UpdateMfaTotpState
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseTOTPCreate(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val request: PerformRequest<TOTPCreateResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            dispatch(
                UpdateMfaTotpState(
                    MFATOTPState(isCreating = true),
                ),
            )
            request {
                StytchB2BClient.totp.create(
                    TOTP.CreateParameters(
                        organizationId = state.value.mfaPrimaryInfoState?.organizationId ?: "",
                        memberId = state.value.mfaPrimaryInfoState?.memberId ?: "",
                    ),
                )
            }.onSuccess {
                dispatch(SetPostAuthScreen(Routes.RecoveryCodeSave))
                dispatch(
                    UpdateMfaTotpState(
                        MFATOTPState(
                            isCreating = false,
                            isEnrolling = true,
                            enrollmentState =
                                TOTPEnrollmentState(
                                    secret = it.secret,
                                    qrCode = it.qrCode,
                                    recoveryCodes = it.recoveryCodes,
                                ),
                        ),
                    ),
                )
            }.onFailure {
                dispatch(
                    UpdateMfaTotpState(
                        MFATOTPState(
                            isCreating = false,
                            isEnrolling = false,
                            error = it.toStytchError(),
                            enrollmentState = null,
                        ),
                    ),
                )
            }
        }
    }
}

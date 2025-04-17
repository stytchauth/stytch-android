package com.stytch.sdk.ui.b2b.screens.smsOTPEntry

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.MFASMSState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSSend
import com.stytch.sdk.ui.shared.data.PhoneNumberState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class SMSOTPEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useOTPSMSSend = UseOTPSMSSend(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useOTPSMSAuthenticate = UseOTPSMSAuthenticate(viewModelScope, productConfig, state, ::request)

    val smsOtpEntryState =
        state
            .map {
                SMSOTPEntryScreenState(
                    phoneNumberState = state.value.phoneNumberState,
                    mfaSMSState = state.value.mfaSMSState,
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                SMSOTPEntryScreenState(
                    phoneNumberState = state.value.phoneNumberState,
                    mfaSMSState = state.value.mfaSMSState,
                ),
            )

    fun handle(action: SMSOTPEntryAction) {
        when (action) {
            is SMSOTPEntryAction.Authenticate -> useOTPSMSAuthenticate(code = action.code)
            is SMSOTPEntryAction.Send -> useOTPSMSSend(isEnrolling = action.isEnrolling)
            SMSOTPEntryAction.GoToSMSEnrollment -> dispatch(SetNextRoute(Routes.SMSOTPEnrollment))
        }
    }

    init {
        val smsImplicitlySent = state.value.mfaPrimaryInfoState?.smsImplicitlySent == true
        val didSend = state.value.mfaSMSState?.didSend == true
        // If we haven't already sent one ourselves, and an SMS message was not already sent by the server, send it now
        if (!didSend && !smsImplicitlySent) {
            useOTPSMSSend(false)
        }
    }
}

@JacocoExcludeGenerated
internal data class SMSOTPEntryScreenState(
    val phoneNumberState: PhoneNumberState,
    val mfaSMSState: MFASMSState?,
)

internal sealed class SMSOTPEntryAction {
    data object GoToSMSEnrollment : SMSOTPEntryAction()

    @JacocoExcludeGenerated
    data class Authenticate(
        val code: String,
    ) : SMSOTPEntryAction()

    @JacocoExcludeGenerated
    data class Send(
        val isEnrolling: Boolean,
    ) : SMSOTPEntryAction()
}

package com.stytch.sdk.ui.b2b.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.components.ResendableOTP
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSSend
import kotlinx.coroutines.flow.StateFlow

internal class SMSOTPEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useOTPSMSSend = UseOTPSMSSend(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useOTPSMSAuthenticate = UseOTPSMSAuthenticate(viewModelScope, productConfig, state, ::request)

    init {
        val smsImplicitlySent = state.value.mfaPrimaryInfoState?.smsImplicitlySent == true
        val didSend = state.value.mfaSMSState?.didSend == true
        // If we haven't already sent one ourselves, and an SMS message was not already sent by the server, send it now
        if (!didSend && !smsImplicitlySent) {
            useOTPSMSSend(false)
        }
    }
}

@Composable
internal fun SMSOTPEntryScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<SMSOTPEntryScreenViewModel>,
    viewModel: SMSOTPEntryScreenViewModel = createViewModel(SMSOTPEntryScreenViewModel::class.java),
) {
    val recipientFormatted =
        AnnotatedString(
            text = " ${state.value.phoneNumberState.formatted()}",
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    val isEnrolling = state.value.mfaSMSState?.isEnrolling ?: false
    ResendableOTP(
        title = "Enter passcode",
        recipient = recipientFormatted,
        isEnrolling = isEnrolling,
        onBack = { viewModel.dispatch(SetNextRoute(Routes.SMSOTPEnrollment)) },
        onSubmit = { viewModel.useOTPSMSAuthenticate(it) },
        onResend = { viewModel.useOTPSMSSend(isEnrolling) },
    )
}

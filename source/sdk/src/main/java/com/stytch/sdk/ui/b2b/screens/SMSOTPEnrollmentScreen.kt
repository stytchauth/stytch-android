package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSSend
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberPhoneNumber
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.PhoneEntry
import kotlinx.coroutines.flow.StateFlow

internal class SMSOTPEnrollmentScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val useSmsOtpSend = UseOTPSMSSend(viewModelScope, state, ::dispatch, ::request)
    val useUpdateMemberPhoneNumber = UseUpdateMemberPhoneNumber(state, ::dispatch)
}

@Composable
internal fun SMSOTPEnrollmentScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<SMSOTPEnrollmentScreenViewModel>,
    viewModel: SMSOTPEnrollmentScreenViewModel = createViewModel(SMSOTPEnrollmentScreenViewModel::class.java),
) {
    val phoneNumberState = state.value.phoneNumberState
    Column {
        BackButton(onClick = { viewModel.dispatch(SetNextRoute(Routes.MFAEnrollmentSelection)) })
        PageTitle(textAlign = TextAlign.Left, text = "Enter your phone number to set up Multi-Factor Authentication")
        BodyText(
            text = "Your organization requires an additional form of verification to make your account more secure.",
        )
        PhoneEntry(
            countryCode = phoneNumberState.countryCode,
            onCountryCodeChanged = { viewModel.useUpdateMemberPhoneNumber(it, null) },
            phoneNumber = phoneNumberState.phoneNumber,
            onPhoneNumberChanged = { viewModel.useUpdateMemberPhoneNumber(null, it) },
            onPhoneNumberSubmit = { viewModel.useSmsOtpSend() },
        )
    }
}

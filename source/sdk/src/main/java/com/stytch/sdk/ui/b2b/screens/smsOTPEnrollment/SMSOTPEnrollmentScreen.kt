package com.stytch.sdk.ui.b2b.screens.smsOTPEnrollment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.PhoneEntry
import com.stytch.sdk.ui.shared.data.PhoneNumberState

@Composable
internal fun SMSOTPEnrollmentScreen(viewModel: SMSOTPEnrollmentScreenViewModel) {
    val state = viewModel.phoneNumberState.collectAsState()
    SMSOTPEnrollmentScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun SMSOTPEnrollmentScreenComposable(
    state: PhoneNumberState,
    dispatch: (SMSOTPEnrollmentAction) -> Unit,
) {
    BackHandler(enabled = true) {
        dispatch(SMSOTPEnrollmentAction.DispatchGlobalAction(SetNextRoute(Routes.MFAEnrollmentSelection)))
    }
    Column {
        BackButton(onClick = {
            dispatch(SMSOTPEnrollmentAction.DispatchGlobalAction(SetNextRoute(Routes.MFAEnrollmentSelection)))
        })
        PageTitle(textAlign = TextAlign.Left, text = "Enter your phone number to set up Multi-Factor Authentication")
        BodyText(
            text = "Your organization requires an additional form of verification to make your account more secure.",
        )
        PhoneEntry(
            countryCode = state.countryCode,
            onCountryCodeChanged = { dispatch(SMSOTPEnrollmentAction.UpdateMemberCountryCode(it)) },
            phoneNumber = state.phoneNumber,
            onPhoneNumberChanged = { dispatch(SMSOTPEnrollmentAction.UpdateMemberPhoneNumber(it)) },
            onPhoneNumberSubmit = { dispatch(SMSOTPEnrollmentAction.Send(true)) },
        )
    }
}

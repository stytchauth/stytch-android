package com.stytch.sdk.ui.b2b.screens.smsOTPEntry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.components.ResendableOTP

@Composable
internal fun SMSOTPEntryScreen(viewModel: SMSOTPEntryScreenViewModel) {
    val state = viewModel.smsOtpEntryState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onScreenLoad()
    }
    SMSOTPEntryScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun SMSOTPEntryScreenComposable(
    state: SMSOTPEntryScreenState,
    dispatch: (SMSOTPEntryAction) -> Unit,
) {
    val recipientFormatted =
        AnnotatedString(
            text = " ${state.phoneNumberState.formatted()}",
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    val isEnrolling = state.mfaSMSState?.isEnrolling ?: false
    ResendableOTP(
        title = stringResource(R.string.stytch_b2b_enter_passcode),
        recipient = recipientFormatted,
        isEnrolling = isEnrolling,
        onBack = { dispatch(SMSOTPEntryAction.GoToSMSEnrollment) },
        onSubmit = { dispatch(SMSOTPEntryAction.Authenticate(it)) },
        onResend = { dispatch(SMSOTPEntryAction.Send(isEnrolling = isEnrolling)) },
    )
}

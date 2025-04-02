package com.stytch.sdk.ui.b2b.screens.emailOTPEntry

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.ui.b2b.components.ResendableOTP

@Composable
internal fun EmailOTPEntryScreen(viewModel: EmailOTPEntryScreenViewModel) {
    val emailOtpEntryState = viewModel.emailOtpEntryState.collectAsStateWithLifecycle()
    EmailOTPEntryScreenComposable(
        state = emailOtpEntryState.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun EmailOTPEntryScreenComposable(
    state: EmailOTPEntryScreenState,
    dispatch: (EmailOTPEntryScreenAction) -> Unit,
) {
    ResendableOTP(
        title = "Enter verification code",
        recipient = state.recipientFormatted,
        errorMessage = state.errorMessage,
        isEnrolling = false,
        onBack = null,
        onSubmit = { dispatch(EmailOTPEntryScreenAction.Submit(it)) },
        onResend = { dispatch(EmailOTPEntryScreenAction.Resend) },
        otpExpirationMinutes = 10,
    )
}

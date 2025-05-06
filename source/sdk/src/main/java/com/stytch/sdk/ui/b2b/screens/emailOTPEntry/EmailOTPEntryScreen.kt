package com.stytch.sdk.ui.b2b.screens.emailOTPEntry

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.R
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
        title = stringResource(R.string.stytch_b2b_enter_verification_code),
        recipient = state.recipientFormatted,
        errorMessage = state.errorMessage?.let { stringResource(it) },
        isEnrolling = false,
        onBack = null,
        onSubmit = { dispatch(EmailOTPEntryScreenAction.Submit(it)) },
        onResend = { dispatch(EmailOTPEntryScreenAction.Resend) },
        otpExpirationMinutes = 10,
    )
}

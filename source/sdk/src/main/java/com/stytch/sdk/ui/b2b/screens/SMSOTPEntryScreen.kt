package com.stytch.sdk.ui.b2b.screens

import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseOTPSMSSend
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.OTPEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchAlertDialog
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val OTP_EXPIRATION_SECONDS = (2 * 60).toLong()
private const val ONE_SECOND = 1000L

internal class SMSOTPEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    val useOTPSMSSend = UseOTPSMSSend(viewModelScope, state, ::dispatch, ::request)
    val useOTPSMSAuthenticate = UseOTPSMSAuthenticate(viewModelScope, state, ::request)

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
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val recipientFormatted =
        AnnotatedString(
            text = " ${state.value.phoneNumberState.formatted()}",
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    var showResendDialog by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableLongStateOf(OTP_EXPIRATION_SECONDS) }
    var expirationTimeFormatted by remember { mutableStateOf("2:00") }
    val coroutineScope = rememberCoroutineScope()
    val isEnrolling = state.value.mfaSMSState?.isEnrolling ?: false
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (countdownSeconds > 0) {
                delay(ONE_SECOND)
                countdownSeconds -= 1
                expirationTimeFormatted = DateUtils.formatElapsedTime(countdownSeconds)
            }
        }
    }
    BackHandler(enabled = isEnrolling) {
        viewModel.dispatch(SetNextRoute(Routes.SMSOTPEnrollment))
    }
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        if (isEnrolling) {
            BackButton {
                viewModel.dispatch(SetNextRoute(Routes.SMSOTPEnrollment))
            }
        }
        PageTitle(textAlign = TextAlign.Left, text = "Enter passcode")
        BodyText(
            text =
                buildAnnotatedString {
                    append("A 6-digit passcode was sent to you at ")
                    append(recipientFormatted)
                },
        )
        OTPEntry(onCodeComplete = { viewModel.useOTPSMSAuthenticate(it) })
        Text(
            text = stringResource(id = R.string.code_expires_in, expirationTimeFormatted),
            textAlign = TextAlign.Start,
            style =
                type.caption.copy(
                    color = Color(theme.secondaryTextColor),
                ),
            modifier = Modifier.clickable { showResendDialog = true },
        )
    }
    if (showResendDialog) {
        StytchAlertDialog(
            onDismissRequest = { showResendDialog = false },
            title = stringResource(id = R.string.resend_code),
            body =
                buildAnnotatedString {
                    append(stringResource(id = R.string.new_code_will_be_sent_to))
                    append(recipientFormatted)
                },
            cancelText = stringResource(id = R.string.cancel),
            onCancelClick = { showResendDialog = false },
            acceptText = stringResource(id = R.string.send_code),
            onAcceptClick = {
                viewModel.useOTPSMSSend(isEnrolling)
                countdownSeconds = OTP_EXPIRATION_SECONDS
                showResendDialog = false
            },
        )
    }
}

package com.stytch.sdk.ui.screens

import android.os.Parcelable
import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.components.Body2Text
import com.stytch.sdk.ui.components.BodyText
import com.stytch.sdk.ui.components.OTPEntry
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.data.SessionOptions
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class OTPConfirmationScreen(
    val resendParameters: OTPDetails,
    val sessionOptions: SessionOptions,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<OTPConfirmationScreenViewModel>()
        OTPConfirmationScreenComposable(
            viewModel = viewModel,
            resendParameters = resendParameters,
            sessionOptions = sessionOptions,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OTPConfirmationScreenComposable(
    viewModel: OTPConfirmationScreenViewModel,
    resendParameters: OTPDetails,
    sessionOptions: SessionOptions,
) {
    val context = LocalContext.current as AuthenticationActivity
    val navigator = LocalNavigator.currentOrThrow
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val recipient = when (resendParameters) {
        is OTPDetails.EmailOTP -> resendParameters.parameters.email
        is OTPDetails.SmsOTP -> resendParameters.parameters.phoneNumber
        is OTPDetails.WhatsAppOTP -> resendParameters.parameters.phoneNumber
    }
    fun getExpirationSeconds() = (
        when (resendParameters) {
            is OTPDetails.EmailOTP -> resendParameters.parameters.expirationMinutes
            is OTPDetails.SmsOTP -> resendParameters.parameters.expirationMinutes
            is OTPDetails.WhatsAppOTP -> resendParameters.parameters.expirationMinutes
        } * 60U
        ).toLong()
    var methodId = remember {
        when (resendParameters) {
            is OTPDetails.EmailOTP -> resendParameters.methodId
            is OTPDetails.SmsOTP -> resendParameters.methodId
            is OTPDetails.WhatsAppOTP -> resendParameters.methodId
        }
    }
    val recipientFormatted = AnnotatedString(
        text = " $recipient",
        spanStyle = SpanStyle(fontWeight = FontWeight.W700)
    )
    var showResendDialog by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(getExpirationSeconds()) }
    var countdownTimeFormat by remember { mutableStateOf(DateUtils.formatElapsedTime(countdownSeconds)) }
    val confirmationState = viewModel.confirmationState.collectAsState()
    var showLoadingOverlay by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = countdownSeconds) {
        if (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds -= 1
            countdownTimeFormat = DateUtils.formatElapsedTime(countdownSeconds)
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.didResend.collectLatest {
            if (it.isNotBlank()) {
                methodId = it
                countdownSeconds = getExpirationSeconds()
                countdownTimeFormat = DateUtils.formatElapsedTime(countdownSeconds)
            }
        }
    }
    LaunchedEffect(key1 = confirmationState.value) {
        when (val state = confirmationState.value) {
            is ConfirmationState.Confirmed -> {
                showLoadingOverlay = false
                context.returnAuthenticationResult(state.result)
            }
            is ConfirmationState.Failed -> showLoadingOverlay = false
            else -> {}
        }
    }
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton { navigator.pop() }
        PageTitle(
            text = stringResource(id = R.string.enter_passcode),
            textAlign = TextAlign.Start,
        )
        BodyText(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.passcode_sent_to))
                append(recipientFormatted)
            }
        )
        OTPEntry(
            errorMessage = (confirmationState.value as? ConfirmationState.Failed)?.message,
            onCodeComplete = {
                showLoadingOverlay = true
                viewModel.authenticateOTP(it, methodId, sessionOptions)
            }
        )
        Text(
            text = stringResource(id = R.string.code_expires_in, countdownTimeFormat),
            textAlign = TextAlign.Start,
            style = type.caption.copy(
                color = Color(theme.secondaryTextColor)
            ),
            modifier = Modifier.clickable { showResendDialog = true }
        )
    }
    if (showLoadingOverlay) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator(
                color = Color(theme.inputTextColor)
            )
        }
    }
    if (showResendDialog) {
        AlertDialog(
            onDismissRequest = { showResendDialog = false },
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(size = 28.dp),
                tonalElevation = AlertDialogDefaults.TonalElevation,
                color = Color.White,
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    PageTitle(
                        text = stringResource(id = R.string.resend_code),
                        textAlign = TextAlign.Start,
                        color = Color(theme.dialogTextColor)
                    )
                    Body2Text(
                        text = buildAnnotatedString {
                            append(stringResource(id = R.string.new_code_will_be_sent_to))
                            append(recipientFormatted)
                        },
                        color = Color(theme.dialogTextColor)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        TextButton(onClick = { showResendDialog = false }) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                style = type.body2.copy(
                                    color = Color(theme.buttonTextColor)
                                )
                            )
                        }
                        TextButton(
                            onClick = {
                                showResendDialog = false
                                viewModel.resendOTP(resendParameters)
                            },
                        ) {
                            Text(
                                text = stringResource(id = R.string.send_code),
                                style = type.body2.copy(
                                    color = Color(theme.buttonTextColor)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

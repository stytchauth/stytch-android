package com.stytch.sdk.ui.b2b.screens

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoveryAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPLoginOrSignup
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.OTPEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchAlertDialog
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class EmailOTPEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val _emailOtpEntryState =
        MutableStateFlow(
            EmailOTPEntryScreenState(
                emailAddress = state.value.emailState.emailAddress,
            ),
        )
    val emailOtpEntryState = _emailOtpEntryState.asStateFlow()
    val useEmailOTPLoginOrSignup = UseEmailOTPLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useEmailOTPDiscoverySend = UseEmailOTPDiscoverySend(viewModelScope, state, ::dispatch, productConfig, ::request)
    val useEmailOTPAuthenticate = UseEmailOTPAuthenticate(productConfig, state, ::request)
    val useEmailOTPDiscoveryAuthenticate = UseEmailOTPDiscoveryAuthenticate(state, ::request)

    fun handleSubmit(code: String) {
        viewModelScope.launch {
            if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
                useEmailOTPAuthenticate(code).onFailure {
                    handleFailure()
                }
            } else {
                useEmailOTPDiscoveryAuthenticate(code)
                    .onSuccess {
                        dispatch(SetNextRoute(Routes.Discovery))
                    }.onFailure {
                        handleFailure()
                    }
            }
        }
    }

    fun handleResend() {
        if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
            useEmailOTPLoginOrSignup()
        } else {
            useEmailOTPDiscoverySend()
        }
    }

    private fun handleFailure() {
        _emailOtpEntryState.value =
            _emailOtpEntryState.value.copy(
                errorMessage = "Invalid passcode, please try again.",
            )
    }
}

internal data class EmailOTPEntryScreenState(
    val emailAddress: String,
    val errorMessage: String? = null,
)

@Composable
internal fun EmailOTPEntryScreen(
    createViewModel: CreateViewModel<EmailOTPEntryScreenViewModel>,
    viewModel: EmailOTPEntryScreenViewModel =
        createViewModel(EmailOTPEntryScreenViewModel::class.java),
) {
    val emailOtpEntryState = viewModel.emailOtpEntryState.collectAsStateWithLifecycle().value
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val recipientFormatted =
        AnnotatedString(
            text = " ${emailOtpEntryState.emailAddress}",
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    var showResendDialog by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableLongStateOf(OTP_EXPIRATION_SECONDS) }
    var expirationTimeFormatted by remember { mutableStateOf("2:00") }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (countdownSeconds > 0) {
                delay(ONE_SECOND)
                countdownSeconds -= 1
                expirationTimeFormatted = DateUtils.formatElapsedTime(countdownSeconds)
            }
        }
    }
    Column {
        PageTitle(textAlign = TextAlign.Left, text = "Enter verification code")
        BodyText(
            text =
                buildAnnotatedString {
                    append("A 6-digit passcode was sent to you at ")
                    append(recipientFormatted)
                },
        )
        OTPEntry(errorMessage = emailOtpEntryState.errorMessage, onCodeComplete = viewModel::handleSubmit)
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
                viewModel.handleResend()
                countdownSeconds = OTP_EXPIRATION_SECONDS
                showResendDialog = false
            },
        )
    }
}

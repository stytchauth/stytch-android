package com.stytch.sdk.ui.b2b.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.components.ResendableOTP
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
    val recipientFormatted =
        AnnotatedString(
            text = " ${emailOtpEntryState.emailAddress}",
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    ResendableOTP(
        title = "Enter verification code",
        recipient = recipientFormatted,
        isEnrolling = false,
        onBack = null,
        onSubmit = viewModel::handleSubmit,
        onResend = viewModel::handleResend,
    )
}

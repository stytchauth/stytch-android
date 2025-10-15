package com.stytch.sdk.ui.b2b.screens.emailOTPEntry

import androidx.annotation.StringRes
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.R
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetDiscoveredOrganizations
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoveryAuthenticate
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPLoginOrSignup
import com.stytch.sdk.ui.shared.utils.getUserFacingErrorMessageId
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
                recipientFormatted =
                    AnnotatedString(
                        text = " ${state.value.emailState.emailAddress}",
                        spanStyle = SpanStyle(fontWeight = FontWeight.W700),
                    ),
            ),
        )
    val emailOtpEntryState = _emailOtpEntryState.asStateFlow()
    private val useEmailOTPLoginOrSignup =
        UseEmailOTPLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useEmailOTPDiscoverySend =
        UseEmailOTPDiscoverySend(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useEmailOTPAuthenticate = UseEmailOTPAuthenticate(productConfig, state, ::request)
    private val useEmailOTPDiscoveryAuthenticate = UseEmailOTPDiscoveryAuthenticate(state, ::request)

    private fun handleSubmit(code: String) {
        viewModelScope.launch {
            if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
                useEmailOTPAuthenticate(code).onFailure {
                    handleFailure(it)
                }
            } else {
                useEmailOTPDiscoveryAuthenticate(code)
                    .onSuccess {
                        dispatch(SetDiscoveredOrganizations(it.discoveredOrganizations))
                        dispatch(SetNextRoute(Routes.Discovery))
                    }.onFailure {
                        handleFailure(it)
                    }
            }
        }
    }

    private fun handleResend() {
        if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
            useEmailOTPLoginOrSignup()
        } else {
            useEmailOTPDiscoverySend()
        }
    }

    private fun handleFailure(exception: Throwable) {
        _emailOtpEntryState.value =
            _emailOtpEntryState.value.copy(
                errorMessage =
                    (exception as? StytchError)?.getUserFacingErrorMessageId()
                        ?: R.string.stytch_b2b_error_invalid_passcode,
            )
    }

    fun handle(action: EmailOTPEntryScreenAction) {
        when (action) {
            EmailOTPEntryScreenAction.Resend -> handleResend()
            is EmailOTPEntryScreenAction.Submit -> handleSubmit(action.code)
        }
    }
}

@JacocoExcludeGenerated
internal data class EmailOTPEntryScreenState(
    val recipientFormatted: AnnotatedString,
    @StringRes val errorMessage: Int? = null,
)

internal sealed class EmailOTPEntryScreenAction {
    data object Resend : EmailOTPEntryScreenAction()

    @JacocoExcludeGenerated
    data class Submit(
        val code: String,
    ) : EmailOTPEntryScreenAction()
}

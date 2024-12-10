package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.MFAPrimaryInfoState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseTOTPAuthenticate
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.OTPEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class TOTPEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val _totpEntryState =
        MutableStateFlow(
            TOTPEntryScreenState(
                isEnrolling = state.value.mfaTOTPState?.isEnrolling == true,
                isSmsOtpAvailable = isSmsOtpAvailable(state.value.mfaPrimaryInfoState),
            ),
        )
    val totpEntryState = _totpEntryState.asStateFlow()

    private val useTOTPAuthenticate = UseTOTPAuthenticate(state, productConfig, ::request)

    private fun isSmsOtpAvailable(primaryInfoState: MFAPrimaryInfoState?): Boolean {
        if (primaryInfoState == null) return false
        return state.value.mfaTOTPState?.isEnrolling == false &&
            primaryInfoState.enrolledMfaMethods.contains(MfaMethod.SMS) &&
            primaryInfoState.organizationMfaOptionsSupported.contains(MfaMethod.SMS)
    }

    fun useRecoveryCode() {
        dispatch(SetNextRoute(Routes.RecoveryCodeEntry))
    }

    fun textMeACode() {
        dispatch(SetNextRoute(Routes.SMSOTPEntry))
    }

    fun validateCode(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            useTOTPAuthenticate(code = code)
                .onFailure {
                    _totpEntryState.value =
                        _totpEntryState.value.copy(
                            errorMessage = "Failed to validate TOTP code",
                        )
                }
        }
    }
}

internal data class TOTPEntryScreenState(
    val isEnrolling: Boolean,
    val isSmsOtpAvailable: Boolean,
    val errorMessage: String? = null,
)

@Composable
internal fun TOTPEntryScreen(
    createViewModel: CreateViewModel<TOTPEntryScreenViewModel>,
    viewModel: TOTPEntryScreenViewModel = createViewModel(TOTPEntryScreenViewModel::class.java),
) {
    val totpEntryState = viewModel.totpEntryState.collectAsStateWithLifecycle().value
    val theme = LocalStytchTheme.current
    Column {
        PageTitle(textAlign = TextAlign.Left, text = "Enter verification code")
        BodyText(text = "Enter the 6-digit code from your authenticator app.")
        OTPEntry(errorMessage = totpEntryState.errorMessage, onCodeComplete = viewModel::validateCode)

        if (!totpEntryState.isEnrolling) {
            StytchTextButton(
                text =
                    buildAnnotatedString {
                        append("Can’t access your authenticator app? ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Use a backup code")
                        }
                    },
                color = theme.secondaryTextColor,
                onClick = viewModel::useRecoveryCode,
            )
        } else {
            BodyText(
                text = "If the verification code doesn’t work, go back to your authenticator app to get a new code.",
            )
        }

        if (totpEntryState.isSmsOtpAvailable) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(theme.inputBorderColor),
            )
            StytchTextButton(text = "Text me a code instead", onClick = viewModel::textMeACode)
        }
    }
}

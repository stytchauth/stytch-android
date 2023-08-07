package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.components.BodyText
import com.stytch.sdk.ui.components.DividerWithText
import com.stytch.sdk.ui.components.LoadingDialog
import com.stytch.sdk.ui.components.OTPEntry
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.StytchAlertDialog
import com.stytch.sdk.ui.components.StytchTextButton
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class OTPConfirmationScreen(
    val resendParameters: OTPDetails,
    val isReturningUser: Boolean,
    val emailAddress: String? = null,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<OTPConfirmationScreenViewModel>()
        val productConfig = LocalStytchProductConfig.current
        val context = LocalContext.current as AuthenticationActivity
        val navigator = LocalNavigator.currentOrThrow
        val recipient = when (resendParameters) {
            is OTPDetails.EmailOTP -> resendParameters.parameters.email
            is OTPDetails.SmsOTP -> resendParameters.parameters.phoneNumber
            is OTPDetails.WhatsAppOTP -> resendParameters.parameters.phoneNumber
        }
        val uiState = viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.setInitialState(resendParameters)
            viewModel.eventFlow.collectLatest {
                when (it) {
                    is EventState.Authenticated -> context.returnAuthenticationResult(it.result)
                    is EventState.NavigationRequested -> navigator.push(it.navigationRoute.screen)
                    is EventState.Exit -> context.exitWithoutAuthenticating()
                }
            }
        }
        OTPConfirmationScreenComposable(
            uiState = uiState.value,
            productList = productConfig.products,
            recipient = recipient,
            isReturningUser = isReturningUser,
            onBack = navigator::pop,
            onDialogDismiss = viewModel::onDialogDismiss,
            onShowResendDialog = viewModel::onShowResendDialog,
            onResendEML = { viewModel.resendOTP(resendParameters) },
            onOTPCodeComplete = { code -> viewModel.authenticateOTP(code, productConfig.sessionOptions) },
            onCreatePasswordClicked = { viewModel.sendResetPasswordEmail(emailAddress, productConfig.passwordOptions) },
        )
    }
}

@Composable
private fun OTPConfirmationScreenComposable(
    uiState: OTPConfirmationUiState,
    productList: List<StytchProduct>,
    recipient: String,
    isReturningUser: Boolean,
    onBack: () -> Unit,
    onDialogDismiss: () -> Unit,
    onShowResendDialog: () -> Unit,
    onResendEML: () -> Unit,
    onOTPCodeComplete: (String) -> Unit,
    onCreatePasswordClicked: () -> Unit,
) {
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val recipientFormatted = AnnotatedString(
        text = " $recipient",
        spanStyle = SpanStyle(fontWeight = FontWeight.W700),
    )
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.enter_passcode),
            textAlign = TextAlign.Start,
        )
        BodyText(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.passcode_sent_to))
                append(recipientFormatted)
            },
        )
        OTPEntry(
            errorMessage = uiState.genericErrorMessage,
            onCodeComplete = onOTPCodeComplete,
        )
        Text(
            text = stringResource(id = R.string.code_expires_in, uiState.expirationTimeFormatted),
            textAlign = TextAlign.Start,
            style = type.caption.copy(
                color = Color(theme.secondaryTextColor),
            ),
            modifier = Modifier.clickable { onShowResendDialog() },
        )
        if (isReturningUser && productList.contains(StytchProduct.PASSWORDS)) {
            DividerWithText(
                modifier = Modifier.padding(top = 24.dp, bottom = 24.dp),
                text = stringResource(id = R.string.or),
            )
            StytchTextButton(
                text = stringResource(id = R.string.create_password_instead),
                onClick = onCreatePasswordClicked,
            )
        }
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
    if (uiState.showResendDialog) {
        StytchAlertDialog(
            onDismissRequest = onDialogDismiss,
            title = stringResource(id = R.string.resend_code),
            body = buildAnnotatedString {
                append(stringResource(id = R.string.new_code_will_be_sent_to))
                append(recipientFormatted)
            },
            cancelText = stringResource(id = R.string.cancel),
            onCancelClick = onDialogDismiss,
            acceptText = stringResource(id = R.string.send_code),
            onAcceptClick = onResendEML,
        )
    }
}

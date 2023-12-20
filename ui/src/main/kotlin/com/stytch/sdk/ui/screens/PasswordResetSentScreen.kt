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
import com.stytch.sdk.ui.components.FormFieldStatus
import com.stytch.sdk.ui.components.LoadingDialog
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.StytchAlertDialog
import com.stytch.sdk.ui.components.StytchTextButton
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordResetSentScreen(
    val details: PasswordResetDetails,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val productConfig = LocalStytchProductConfig.current
        val context = LocalContext.current as AuthenticationActivity
        val viewModel = viewModel<PasswordResetSentScreenViewModel>(
            factory = PasswordResetSentScreenViewModel.factory(context.savedStateHandle)
        )
        val uiState = viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collectLatest {
                when (it) {
                    is EventState.NavigationRequested -> navigator.push(it.navigationRoute.screen)
                    else -> {}
                }
            }
        }
        PasswordResetSentScreenComposable(
            uiState = uiState.value,
            onBack = navigator::pop,
            emailAddress = details.parameters.email,
            onDialogDismiss = viewModel::onDialogDismiss,
            onShowResendDialog = viewModel::onShowResendDialog,
            onResendPasswordResetStart = { viewModel.onResendPasswordResetStart(details.parameters) },
            resetType = details.resetType,
            hasEML = productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS),
            hasEmailOTP = productConfig.products.contains(StytchProduct.OTP) &&
                productConfig.otpOptions.methods.contains(OTPMethods.EMAIL),
            sendEML = { viewModel.sendEML(details.parameters.email, productConfig.emailMagicLinksOptions) },
            sendEmailOTP = { viewModel.sendEmailOTP(details.parameters.email, productConfig.otpOptions) },
        )
    }
}

@Composable
private fun PasswordResetSentScreenComposable(
    uiState: ApplicationUIState,
    onBack: () -> Unit,
    emailAddress: String,
    onDialogDismiss: () -> Unit,
    onShowResendDialog: () -> Unit,
    onResendPasswordResetStart: () -> Unit,
    resetType: PasswordResetType,
    hasEML: Boolean,
    hasEmailOTP: Boolean,
    sendEML: () -> Unit,
    sendEmailOTP: () -> Unit,
) {
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val recipientFormatted = AnnotatedString(
        text = " $emailAddress",
        spanStyle = SpanStyle(fontWeight = FontWeight.W700),
    )
    val resendLinkFormatted = AnnotatedString(
        text = stringResource(id = R.string.resend_link),
        spanStyle = SpanStyle(fontWeight = FontWeight.W700),
    )
    val pageTitleResource = when (resetType) {
        PasswordResetType.FORGOT_PASSWORD -> R.string.forgot_password
        PasswordResetType.NO_PASSWORD_SET,
        PasswordResetType.BREACHED,
        PasswordResetType.DEDUPE,
        -> R.string.check_email_new_password
    }
    val bodyTextString = when (resetType) {
        PasswordResetType.FORGOT_PASSWORD -> buildAnnotatedString {
            append(stringResource(id = R.string.reset_password_link_sent))
            append(" ")
            append(recipientFormatted)
            append(".")
        }
        PasswordResetType.NO_PASSWORD_SET -> buildAnnotatedString {
            append(stringResource(id = R.string.login_link_sent))
            append(" ")
            append(recipientFormatted)
            append(" ")
            append(stringResource(id = R.string.create_password_for_account))
        }
        PasswordResetType.BREACHED -> buildAnnotatedString {
            append(stringResource(id = R.string.breached_password))
            append(" ")
            append(recipientFormatted)
            append(" ")
            append(stringResource(id = R.string.breached_password_2))
        }
        PasswordResetType.DEDUPE -> buildAnnotatedString {
            append(stringResource(id = R.string.secure_your_account, recipientFormatted))
            append(" ")
            append(recipientFormatted)
            append(".")
        }
    }
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = pageTitleResource),
            textAlign = TextAlign.Start,
        )
        BodyText(text = bodyTextString)
        Text(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.didnt_get_it))
                append(" ")
                append(resendLinkFormatted)
            },
            textAlign = TextAlign.Start,
            style = type.caption.copy(
                color = Color(theme.secondaryTextColor),
            ),
            modifier = Modifier.clickable { onShowResendDialog() },
        )
        uiState.genericErrorMessage?.let {
            FormFieldStatus(text = it, isError = true)
        }
        if (hasEML || hasEmailOTP) {
            DividerWithText(
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                text = stringResource(id = R.string.or),
            )
            StytchTextButton(
                text = stringResource(
                    id = if (hasEML) {
                        R.string.email_me_a_login_link
                    } else {
                        R.string.email_me_a_login_code
                    },
                ),
                onClick = { if (hasEML) sendEML() else sendEmailOTP() },
            )
        }
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
    if (uiState.showResendDialog) {
        StytchAlertDialog(
            onDismissRequest = onDialogDismiss,
            title = stringResource(id = R.string.resend_link),
            body = buildAnnotatedString {
                append(stringResource(id = R.string.new_link_will_be_sent_to))
                append(recipientFormatted)
            },
            cancelText = stringResource(id = R.string.cancel),
            onCancelClick = onDialogDismiss,
            acceptText = stringResource(id = R.string.send_link),
            onAcceptClick = onResendPasswordResetStart,
        )
    }
}

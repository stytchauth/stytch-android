package com.stytch.sdk.ui.b2c.screens

import android.os.Parcelable
import androidx.activity.compose.LocalActivity
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.PasswordResetDetails
import com.stytch.sdk.ui.b2c.data.PasswordResetType
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchAlertDialog
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import com.stytch.sdk.ui.shared.utils.getStyledText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordResetSentScreen(
    val details: PasswordResetDetails,
) : AndroidScreen(),
    Parcelable {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val productConfig = LocalStytchProductConfig.current
        val context = LocalActivity.current as AuthenticationActivity
        val viewModel =
            viewModel<PasswordResetSentScreenViewModel>(
                factory = PasswordResetSentScreenViewModel.factory(context.savedStateHandle),
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
            hasEmailOTP =
                productConfig.products.contains(StytchProduct.OTP) &&
                    productConfig.otpOptions.methods.contains(OTPMethods.EMAIL),
            sendEML = {
                viewModel.sendEML(
                    details.parameters.email,
                    productConfig.emailMagicLinksOptions,
                    productConfig.locale,
                )
            },
            sendEmailOTP = {
                viewModel.sendEmailOTP(
                    details.parameters.email,
                    productConfig.otpOptions,
                    productConfig.locale,
                )
            },
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
    val context = LocalContext.current
    val pageTitleResource =
        when (resetType) {
            PasswordResetType.FORGOT_PASSWORD -> R.string.stytch_b2c_forgot_password
            PasswordResetType.NO_PASSWORD_SET,
            PasswordResetType.BREACHED,
            PasswordResetType.DEDUPE,
            -> R.string.stytch_b2c_check_email_new_password
        }
    val bodyTextString =
        when (resetType) {
            PasswordResetType.FORGOT_PASSWORD ->
                context.getStyledText(
                    R.string.stytch_b2c_reset_password_link_sent,
                    emailAddress,
                )
            PasswordResetType.NO_PASSWORD_SET ->
                context.getStyledText(
                    R.string.stytch_b2c_login_link_sent_to_create_password,
                    emailAddress,
                )
            PasswordResetType.BREACHED -> context.getStyledText(R.string.stytch_b2c_breached_password, emailAddress)
            PasswordResetType.DEDUPE -> context.getStyledText(R.string.stytch_b2c_secure_your_account, emailAddress)
        }
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = pageTitleResource),
            textAlign = TextAlign.Start,
        )
        BodyText(text = bodyTextString)
        Text(
            text =
                buildAnnotatedString {
                    append(stringResource(id = R.string.stytch_b2c_didnt_get_it_resend_link))
                },
            textAlign = TextAlign.Start,
            style =
                type.caption.copy(
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
                text = stringResource(id = R.string.stytch_b2c_method_divider_text),
            )
            StytchTextButton(
                text =
                    stringResource(
                        id =
                            if (hasEML) {
                                R.string.stytch_b2c_email_me_a_login_link
                            } else {
                                R.string.stytch_b2c_email_me_a_login_code
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
            title = stringResource(id = R.string.stytch_b2c_resend_link_title),
            body = context.getStyledText(R.string.stytch_b2c_new_link_will_be_sent_to, emailAddress),
            cancelText = stringResource(id = R.string.stytch_cancel),
            onCancelClick = onDialogDismiss,
            acceptText = stringResource(id = R.string.stytch_b2c_send_link),
            onAcceptClick = onResendPasswordResetStart,
        )
    }
}

package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.components.BodyText
import com.stytch.sdk.ui.components.FormFieldStatus
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.StytchAlertDialog
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordResetSentScreen(
    val details: PasswordResetDetails
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<PasswordResetSentScreenViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val uiState = viewModel.uiState.collectAsState()
        PasswordResetSentScreenComposable(
            uiState = uiState.value,
            onBack = navigator::pop,
            emailAddress = details.parameters.email,
            onDialogDismiss = viewModel::onDialogDismiss,
            onShowResendDialog = viewModel::onShowResendDialog,
            onResendPasswordResetStart = { viewModel.onResendPasswordResetStart(details.parameters) },
        )
    }
}

@Composable
private fun PasswordResetSentScreenComposable(
    uiState: PasswordResetUiState,
    onBack: () -> Unit,
    emailAddress: String,
    onDialogDismiss: () -> Unit,
    onShowResendDialog: () -> Unit,
    onResendPasswordResetStart: () -> Unit,
) {
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val recipientFormatted = AnnotatedString(
        text = " $emailAddress",
        spanStyle = SpanStyle(fontWeight = FontWeight.W700)
    )
    val resendLinkFormatted = AnnotatedString(
        text = stringResource(id = R.string.resend_link),
        spanStyle = SpanStyle(fontWeight = FontWeight.W700)
    )
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.check_email_new_password),
            textAlign = TextAlign.Start,
        )
        BodyText(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.login_link_sent))
                append(" ")
                append(recipientFormatted)
                append(" ")
                append(stringResource(id = R.string.create_password_for_account))
            }
        )
        Text(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.didnt_get_it))
                append(" ")
                append(resendLinkFormatted)
            },
            textAlign = TextAlign.Start,
            style = type.caption.copy(
                color = Color(theme.secondaryTextColor)
            ),
            modifier = Modifier.clickable { onShowResendDialog() }
        )
        uiState.genericErrorMessage?.let {
            FormFieldStatus(text = it, isError = true)
        }
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
            onAcceptClick = onResendPasswordResetStart
        )
    }
}

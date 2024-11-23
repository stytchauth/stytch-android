package com.stytch.sdk.ui.b2c.screens

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
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EMLDetails
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchAlertDialog
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class EMLConfirmationScreen(
    val details: EMLDetails,
    val isReturningUser: Boolean,
) : AndroidScreen(),
    Parcelable {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val productConfig = LocalStytchProductConfig.current
        val context = LocalContext.current as AuthenticationActivity
        val viewModel =
            viewModel<EMLConfirmationScreenViewModel>(
                factory = EMLConfirmationScreenViewModel.factory(context.savedStateHandle),
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
        EMLConfirmationScreenComposable(
            emailAddress = details.parameters.email,
            uiState = uiState.value,
            onBack = navigator::pop,
            onDialogDismiss = viewModel::onDialogDismiss,
            onShowResendDialog = viewModel::onShowResendDialog,
            onResendEML = { viewModel.resendEML(details.parameters) },
            isReturningUser = isReturningUser,
            onCreatePasswordClicked = {
                viewModel.sendResetPasswordEmail(details.parameters.email, productConfig.passwordOptions)
            },
            productList = productConfig.products,
        )
    }
}

@Composable
private fun EMLConfirmationScreenComposable(
    emailAddress: String,
    uiState: ApplicationUIState,
    onBack: () -> Unit,
    onDialogDismiss: () -> Unit,
    onShowResendDialog: () -> Unit,
    onResendEML: () -> Unit,
    isReturningUser: Boolean,
    onCreatePasswordClicked: () -> Unit,
    productList: List<StytchProduct>,
) {
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val recipientFormatted =
        AnnotatedString(
            text = " $emailAddress",
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    val resendLinkFormatted =
        AnnotatedString(
            text = stringResource(id = R.string.resend_link),
            spanStyle = SpanStyle(fontWeight = FontWeight.W700),
        )
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.check_your_email),
            textAlign = TextAlign.Start,
        )
        BodyText(
            text =
                buildAnnotatedString {
                    append(stringResource(id = R.string.login_link_sent))
                    append(recipientFormatted)
                },
        )
        Text(
            text =
                buildAnnotatedString {
                    append(stringResource(id = R.string.didnt_get_it))
                    append(" ")
                    append(resendLinkFormatted)
                },
            textAlign = TextAlign.Start,
            style =
                type.caption.copy(
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
        uiState.genericErrorMessage?.let {
            FormFieldStatus(text = it, isError = true)
        }
    }
    if (uiState.showResendDialog) {
        StytchAlertDialog(
            onDismissRequest = onDialogDismiss,
            title = stringResource(id = R.string.resend_link),
            body =
                buildAnnotatedString {
                    append(stringResource(id = R.string.new_link_will_be_sent_to))
                    append(recipientFormatted)
                },
            cancelText = stringResource(id = R.string.cancel),
            onCancelClick = onDialogDismiss,
            acceptText = stringResource(id = R.string.send_link),
            onAcceptClick = onResendEML,
        )
    }
}

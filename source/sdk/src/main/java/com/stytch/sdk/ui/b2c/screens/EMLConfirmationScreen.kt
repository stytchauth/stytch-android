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
import com.stytch.sdk.ui.shared.utils.getStyledText
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
        val context = LocalActivity.current as AuthenticationActivity
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
                viewModel.sendResetPasswordEmail(
                    details.parameters.email,
                    productConfig.passwordOptions,
                    productConfig.locale,
                )
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
    val context = LocalContext.current
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.stytch_b2c_check_your_email),
            textAlign = TextAlign.Start,
        )
        BodyText(
            text = context.getStyledText(R.string.stytch_b2c_login_link_sent_to_create_password, emailAddress),
        )
        Text(
            text = context.getStyledText(R.string.stytch_b2c_didnt_get_it_resend_link),
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
                text = stringResource(id = R.string.stytch_b2c_method_divider_text),
            )
            StytchTextButton(
                text = stringResource(id = R.string.stytch_b2c_create_password_instead),
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
            title = stringResource(id = R.string.stytch_b2c_resend_link_title),
            body = context.getStyledText(R.string.stytch_b2c_new_link_will_be_sent_to, emailAddress),
            cancelText = stringResource(id = R.string.stytch_cancel),
            onCancelClick = onDialogDismiss,
            acceptText = stringResource(id = R.string.stytch_b2c_send_link),
            onAcceptClick = onResendEML,
        )
    }
}

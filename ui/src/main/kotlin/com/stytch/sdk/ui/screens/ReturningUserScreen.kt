package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.components.DividerWithText
import com.stytch.sdk.ui.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.components.FormFieldStatus
import com.stytch.sdk.ui.components.LoadingDialog
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.StytchTextButton
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal object ReturningUserScreen : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val productConfig = LocalStytchProductConfig.current
        val context = LocalContext.current as AuthenticationActivity
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            viewModel<ReturningUserScreenViewModel>(
                factory = ReturningUserScreenViewModel.factory(context.savedStateHandle),
            )
        val uiState = viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collectLatest {
                when (it) {
                    is EventState.Authenticated -> context.returnAuthenticationResult(it.result)
                    is EventState.NavigationRequested -> navigator.push(it.navigationRoute.screen)
                    is EventState.Exit -> context.exitWithoutAuthenticating()
                }
            }
        }
        ReturningUserScreenComposable(
            uiState = uiState.value,
            hasEML = productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS),
            hasEmailOTP =
                productConfig.products.contains(StytchProduct.OTP) &&
                    productConfig.otpOptions.methods.contains(OTPMethods.EMAIL),
            onBack = navigator::pop,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onEmailAndPasswordSubmitted = {
                viewModel.authenticate(productConfig.sessionOptions, productConfig.passwordOptions)
            },
            sendEML = { viewModel.sendEML(productConfig.emailMagicLinksOptions) },
            sendEmailOTP = { viewModel.sendEmailOTP(productConfig.otpOptions) },
            onForgotPasswordClicked = { viewModel.onForgotPasswordClicked(productConfig.passwordOptions) },
        )
    }
}

@Composable
private fun ReturningUserScreenComposable(
    uiState: ApplicationUIState,
    hasEML: Boolean,
    hasEmailOTP: Boolean,
    onBack: () -> Unit,
    onEmailAddressChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onEmailAndPasswordSubmitted: () -> Unit,
    sendEML: () -> Unit,
    sendEmailOTP: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
) {
    val emailState = uiState.emailState
    val passwordState = uiState.passwordState

    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.log_in),
            textAlign = TextAlign.Start,
        )
        EmailAndPasswordEntry(
            emailState = emailState,
            onEmailAddressChanged = onEmailAddressChanged,
            passwordState = passwordState,
            onPasswordChanged = onPasswordChanged,
            onSubmit = onEmailAndPasswordSubmitted,
        )
        Spacer(modifier = Modifier.height(24.dp))
        StytchTextButton(
            text = stringResource(id = R.string.forgot_password),
            onClick = onForgotPasswordClicked,
        )
        if (hasEML || hasEmailOTP) {
            DividerWithText(
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                text = stringResource(id = R.string.or),
            )
            StytchTextButton(
                text =
                    stringResource(
                        id =
                            if (hasEML) {
                                R.string.email_me_a_login_link
                            } else {
                                R.string.email_me_a_login_code
                            },
                    ),
                onClick = { if (hasEML) sendEML() else sendEmailOTP() },
            )
        }
    }
    uiState.genericErrorMessage?.let {
        FormFieldStatus(text = it, isError = true)
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

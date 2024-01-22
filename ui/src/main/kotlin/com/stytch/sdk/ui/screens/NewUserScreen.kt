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
import androidx.compose.ui.text.AnnotatedString
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
import com.stytch.sdk.ui.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.components.LoadingDialog
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.StytchButton
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.OTPMethods
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal object NewUserScreen : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val productConfig = LocalStytchProductConfig.current
        val context = LocalContext.current as AuthenticationActivity
        val hasEML = productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)
        val hasEmailOTP = productConfig.products.contains(StytchProduct.OTP) &&
            productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
        val viewModel = viewModel<NewUserScreenViewModel>(
            factory = NewUserScreenViewModel.factory(context.savedStateHandle)
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
        NewUserScreenComposable(
            uiState = uiState.value,
            onBack = navigator::pop,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onEmailAndPasswordSubmitted = {
                viewModel.createAccountWithPassword(productConfig.sessionOptions.sessionDurationMinutes.toUInt())
            },
            hasEML = hasEML,
            hasEmailOTP = hasEmailOTP,
            onSendEML = { viewModel.sendEmailMagicLink(productConfig.emailMagicLinksOptions) },
            onSendEmailOTP = { viewModel.sendEmailOTP(productConfig.otpOptions) },
        )
    }
}

@Composable
private fun NewUserScreenComposable(
    uiState: ApplicationUIState,
    onBack: () -> Unit,
    onEmailAddressChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onEmailAndPasswordSubmitted: () -> Unit,
    hasEML: Boolean,
    hasEmailOTP: Boolean,
    onSendEML: () -> Unit,
    onSendEmailOTP: () -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        if (hasEML || hasEmailOTP) {
            PageTitle(
                text = stringResource(id = R.string.choose_how),
                textAlign = TextAlign.Start,
            )
            StytchButton(
                enabled = true,
                text = if (hasEML) {
                    stringResource(id = R.string.email_me_a_login_link)
                } else {
                    stringResource(id = R.string.email_me_a_login_code)
                },
                onClick = { if (hasEML) onSendEML() else onSendEmailOTP() },
            )
            Spacer(modifier = Modifier.height(24.dp))
            DividerWithText(text = stringResource(id = R.string.or))
            Spacer(modifier = Modifier.height(24.dp))
            BodyText(text = AnnotatedString(stringResource(id = R.string.finish_creating)))
        } else {
            PageTitle(
                text = stringResource(id = R.string.create_account),
                textAlign = TextAlign.Start,
            )
        }
        EmailAndPasswordEntry(
            emailState = uiState.emailState,
            onEmailAddressChanged = onEmailAddressChanged,
            passwordState = uiState.passwordState,
            onPasswordChanged = onPasswordChanged,
            onSubmit = onEmailAndPasswordSubmitted,
        )
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

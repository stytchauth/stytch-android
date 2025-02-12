package com.stytch.sdk.ui.b2c.screens

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
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.OTPMethods
import com.stytch.sdk.ui.b2c.data.StytchProduct
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.DividerWithText
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.theme.LocalStytchProductConfig
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
        val hasEmailOTP =
            productConfig.products.contains(StytchProduct.OTP) &&
                productConfig.otpOptions.methods.contains(OTPMethods.EMAIL)
        val hasPasswords = productConfig.products.contains(StytchProduct.PASSWORDS)
        val viewModel =
            viewModel<NewUserScreenViewModel>(
                factory = NewUserScreenViewModel.factory(context.savedStateHandle),
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
                viewModel.createAccountWithPassword(productConfig.sessionOptions.sessionDurationMinutes)
            },
            hasEML = hasEML,
            hasEmailOTP = hasEmailOTP,
            hasPasswords = hasPasswords,
            onSendEML = { viewModel.sendEmailMagicLink(productConfig.emailMagicLinksOptions, productConfig.locale) },
            onSendEmailOTP = { viewModel.sendEmailOTP(productConfig.otpOptions, productConfig.locale) },
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
    hasPasswords: Boolean,
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
                text =
                    if (hasEML) {
                        stringResource(id = R.string.email_me_a_login_link)
                    } else {
                        stringResource(id = R.string.email_me_a_login_code)
                    },
                onClick = { if (hasEML) onSendEML() else onSendEmailOTP() },
            )
            if (hasPasswords) {
                Spacer(modifier = Modifier.height(24.dp))
                DividerWithText(text = stringResource(id = R.string.or))
                Spacer(modifier = Modifier.height(24.dp))
                BodyText(text = AnnotatedString(stringResource(id = R.string.finish_creating)))
            }
        } else {
            if (hasPasswords) {
                PageTitle(
                    text = stringResource(id = R.string.create_account),
                    textAlign = TextAlign.Start,
                )
            }
        }
        if (hasPasswords) {
            EmailAndPasswordEntry(
                emailState = uiState.emailState,
                onEmailAddressChanged = onEmailAddressChanged,
                passwordState = uiState.passwordState,
                onPasswordChanged = onPasswordChanged,
                onSubmit = onEmailAndPasswordSubmitted,
            )
        }
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

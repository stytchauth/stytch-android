package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.stytch.sdk.ui.data.NavigationState
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal class NewUserWithEMLOrOTPScreen(
    val emailAddress: String,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<CreateAccountViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val productConfig = LocalStytchProductConfig.current
        val uiState = viewModel.uiState.collectAsState()
        val context = LocalContext.current as AuthenticationActivity
        viewModel.setInitialState(emailAddress)
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collectLatest {
                when (it) {
                    is CreateAccountEventState.AccountCreated -> context.returnAuthenticationResult(it.result)
                    is CreateAccountEventState.NavigationRequested -> navigator.push(it.navigationState.getScreen())
                }
            }
        }
        NewUserWithEMLOrOTPScreenComposable(
            uiState = uiState.value,
            onBack = navigator::pop,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onEmailAndPasswordSubmitted = {
                viewModel.createAccountWithPassword(productConfig.sessionOptions.sessionDurationMinutes)
            },
            isEml = productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS),
            onSendEML = { viewModel.sendEmailMagicLink(productConfig.emailMagicLinksOptions) },
            onSendEmailOTP = { viewModel.sendEmailOTP(productConfig.otpOptions) },
        )
    }
}

@Composable
private fun NewUserWithEMLOrOTPScreenComposable(
    onBack: () -> Unit,
    uiState: CreateAccountUiState,
    onEmailAddressChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onEmailAndPasswordSubmitted: () -> Unit,
    isEml: Boolean,
    onSendEML: () -> Unit,
    onSendEmailOTP: () -> Unit,
) {
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current

    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.choose_how),
            textAlign = TextAlign.Start
        )
        StytchButton(
            enabled = true,
            text = if (isEml) {
                stringResource(id = R.string.email_me_a_login_link)
            } else {
                stringResource(id = R.string.email_me_a_login_code)
            },
            onClick = { if (isEml) onSendEML() else onSendEmailOTP() }
        )
        Spacer(modifier = Modifier.height(24.dp))
        DividerWithText(text = stringResource(id = R.string.or))
        Spacer(modifier = Modifier.height(24.dp))
        BodyText(text = AnnotatedString(stringResource(id = R.string.finish_creating)))
        EmailAndPasswordEntry(
            emailState = uiState.emailState,
            onEmailAddressChanged = onEmailAddressChanged,
            passwordState = uiState.passwordState,
            onPasswordChanged = onPasswordChanged,
            onSubmit = onEmailAndPasswordSubmitted
        )
        uiState.passwordState.errorMessage?.let {
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = it,
                style = type.caption.copy(
                    color = Color(theme.errorColor)
                )
            )
        }
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
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
import com.stytch.sdk.ui.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.components.LoadingDialog
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal class NewUserPasswordOnlyScreen(
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
                if (it is EventState.AccountCreated) {
                    context.returnAuthenticationResult(it.result)
                }
            }
        }
        NewUserPasswordOnlyScreenComposable(
            uiState = uiState.value,
            onBack = navigator::pop,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onEmailAndPasswordSubmitted = {
                viewModel.createAccountWithPassword(productConfig.sessionOptions.sessionDurationMinutes)
            }
        )
    }
}

@Composable
private fun NewUserPasswordOnlyScreenComposable(
    uiState: CreateAccountUiState,
    onBack: () -> Unit,
    onEmailAddressChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onEmailAndPasswordSubmitted: () -> Unit,
) {
    val emailState = uiState.emailState
    val passwordState = uiState.passwordState

    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.create_account),
            textAlign = TextAlign.Start
        )
        EmailAndPasswordEntry(
            emailState = emailState,
            onEmailAddressChanged = onEmailAddressChanged,
            passwordState = passwordState,
            onPasswordChanged = onPasswordChanged,
            onSubmit = onEmailAndPasswordSubmitted
        )
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

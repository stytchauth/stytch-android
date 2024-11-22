package com.stytch.sdk.ui.b2c.screens

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
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2c.AuthenticationActivity
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.theme.LocalStytchProductConfig
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.LoadingDialog
import com.stytch.sdk.ui.shared.components.PageTitle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SetPasswordScreen(
    val token: String,
) : AndroidScreen(),
    Parcelable {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current as AuthenticationActivity
        val productConfig = LocalStytchProductConfig.current
        val viewModel =
            viewModel<SetPasswordScreenViewModel>(
                factory = SetPasswordScreenViewModel.factory(context.savedStateHandle),
            )
        val uiState = viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.setEmailReadOnly()
            viewModel.eventFlow.collectLatest {
                when (it) {
                    is EventState.Authenticated -> context.returnAuthenticationResult(it.result)
                    is EventState.NavigationRequested -> navigator.push(it.navigationRoute.screen)
                    is EventState.Exit -> context.exitWithoutAuthenticating()
                }
            }
        }
        SetPasswordScreenComposable(
            uiState = uiState.value,
            onBack = navigator::pop,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            onPasswordChanged = viewModel::onPasswordChanged,
            onSubmit = { viewModel.onSubmit(token, productConfig.sessionOptions) },
        )
    }
}

@Composable
private fun SetPasswordScreenComposable(
    uiState: ApplicationUIState,
    onBack: () -> Unit,
    onEmailAddressChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton(onBack)
        PageTitle(
            text = stringResource(id = R.string.set_new_password),
            textAlign = TextAlign.Start,
        )
        EmailAndPasswordEntry(
            emailState = uiState.emailState,
            onEmailAddressChanged = onEmailAddressChanged,
            passwordState = uiState.passwordState,
            onPasswordChanged = onPasswordChanged,
            onSubmit = onSubmit,
        )
    }
    uiState.genericErrorMessage?.let {
        FormFieldStatus(text = it, isError = true)
    }
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }
}

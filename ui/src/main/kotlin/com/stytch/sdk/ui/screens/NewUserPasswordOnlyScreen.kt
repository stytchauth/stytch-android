package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        viewModel.setInitialEmailState(emailAddress)
        NewUserPasswordOnlyScreenComposable(
            viewModel = viewModel
        )
    }
}

@Composable
private fun NewUserPasswordOnlyScreenComposable(
    viewModel: CreateAccountViewModel
) {
    val productConfig = LocalStytchProductConfig.current
    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current as AuthenticationActivity
    val emailState = viewModel.emailState.collectAsState()
    val passwordState = viewModel.passwordState.collectAsState()
    var showLoadingOverlay by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.passwordCreated.collectLatest {
            showLoadingOverlay = false
            context.returnAuthenticationResult(it)
        }
    }

    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton { navigator.pop() }
        PageTitle(
            text = stringResource(id = R.string.create_account),
            textAlign = TextAlign.Start
        )
        EmailAndPasswordEntry(
            emailState = emailState.value,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            passwordState = passwordState.value,
            onPasswordChanged = viewModel::onPasswordChanged,
            onSubmit = {
                showLoadingOverlay = true
                viewModel.createPassword(productConfig.sessionOptions.sessionDurationMinutes)
            }
        )
    }
    if (showLoadingOverlay) {
        LoadingDialog()
    }
}

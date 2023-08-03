package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.ui.AuthenticationActivity
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.components.EmailAndPasswordEntry
import com.stytch.sdk.ui.components.LoadingDialog
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.data.StytchProductConfig
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal class NewUserPasswordOnlyScreen(
    val emailAddress: String,
    val productConfig: StytchProductConfig,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<CreateAccountViewModel>()
        viewModel.setInitialEmailState(emailAddress)
        NewUserPasswordOnlyScreenComposable(
            productConfig = productConfig,
            viewModel = viewModel
        )
    }
}

@Composable
private fun NewUserPasswordOnlyScreenComposable(
    productConfig: StytchProductConfig,
    viewModel: CreateAccountViewModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current as AuthenticationActivity
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val emailState = viewModel.emailState.collectAsState()
    val passwordState = viewModel.passwordState.collectAsState()
    var showLoadingOverlay by remember { mutableStateOf(false) }
    var passwordCreateFailed by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        viewModel.passwordCreated.collectLatest {
            showLoadingOverlay = false
            when (it) {
                is StytchResult.Success -> context.returnAuthenticationResult(it)
                is StytchResult.Error -> passwordCreateFailed = it.exception.reason.toString() // TODO
            }
        }
    }

    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton { navigator.pop() }
        PageTitle(
            text = stringResource(id = R.string.create_account),
            textAlign = TextAlign.Start
        )
        EmailAndPasswordEntry(
            emailAddress = emailState.value.emailAddress,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            passwordState = passwordState.value,
            onPasswordChanged = viewModel::onPasswordChanged,
            onSubmit = {
                showLoadingOverlay = true
                viewModel.createPassword(productConfig.sessionOptions.sessionDurationMinutes)
            }
        )
        passwordCreateFailed?.let {
            Text(
                modifier = Modifier.padding(bottom = 32.dp),
                text = it,
                style = type.caption.copy(
                    color = Color(theme.errorColor)
                )
            )
        }
    }
    if (showLoadingOverlay) {
        LoadingDialog()
    }
}

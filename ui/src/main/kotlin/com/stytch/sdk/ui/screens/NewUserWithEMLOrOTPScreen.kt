package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.stytch.sdk.ui.data.NextPage
import com.stytch.sdk.ui.data.StytchProduct
import com.stytch.sdk.ui.data.StytchProductConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.parcelize.Parcelize

@Parcelize
internal class NewUserWithEMLOrOTPScreen(
    val emailAddress: String,
    val productConfig: StytchProductConfig,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<NewUserWithEMLOrOTPScreenViewModel>()
        viewModel.setInitialEmailState(emailAddress)
        NewUserWithEMLOrOTPScreenComposable(
            productConfig = productConfig,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun NewUserWithEMLOrOTPScreenComposable(
    productConfig: StytchProductConfig,
    viewModel: NewUserWithEMLOrOTPScreenViewModel,
) {
    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current as AuthenticationActivity
    val isEml = productConfig.products.contains(StytchProduct.EMAIL_MAGIC_LINKS)
    val emailState = viewModel.emailState.collectAsState()
    val passwordState = viewModel.passwordState.collectAsState()
    var showLoadingOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.emailLinkSent.collectLatest {
            showLoadingOverlay = false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.nextPage.collectLatest {
            showLoadingOverlay = false
            when (it) {
                is NextPage.OTPConfirmation -> navigator.push(
                    OTPConfirmationScreen(
                        resendParameters = it.details,
                        sessionOptions = productConfig.sessionOptions,
                    )
                )
                is NextPage.EMLConfirmation -> navigator.push(
                    EMLConfirmationScreen(
                        parameters = it.details.parameters
                    )
                )
                else -> {} // this won't happen
            }
        }
    }

    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton { navigator.pop() }
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
            onClick = {
                showLoadingOverlay = true
                if (isEml) {
                    viewModel.sendEmailMagicLink(productConfig.emailMagicLinksOptions)
                } else {
                    viewModel.sendEmailOTP(productConfig.otpOptions)
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        DividerWithText(text = stringResource(id = R.string.or))
        Spacer(modifier = Modifier.height(24.dp))
        BodyText(text = AnnotatedString(stringResource(id = R.string.finish_creating)))
        EmailAndPasswordEntry(
            emailAddress = emailState.value.emailAddress,
            onEmailAddressChanged = viewModel::onEmailAddressChanged,
            passwordState = passwordState.value,
            onPasswordChanged = viewModel::onPasswordChanged,
            onSubmit = viewModel::createPassword
        )
    }
    if (showLoadingOverlay) {
        LoadingDialog()
    }
}

package com.stytch.exampleapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.HomeViewModel
import com.stytch.exampleapp.R
import com.stytch.exampleapp.theme.Red300

@Composable
fun MainScreen(viewModel: HomeViewModel) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.emailTextState,
            isError = viewModel.showEmailError,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.email))
            },
            onValueChange = {
                viewModel.emailTextState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        if (viewModel.showEmailError) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.please_enter_email),
                style = MaterialTheme.typography.caption.copy(color = Red300),
            )
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.phoneNumberTextState,
            isError = viewModel.showPhoneError,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.mobile_number))
            },
            onValueChange = {
                viewModel.phoneNumberTextState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        if (viewModel.showPhoneError) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.please_enter_phone_number),
                style = MaterialTheme.typography.caption.copy(color = Red300),
            )
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.otpTokenTextState,
            isError = viewModel.showOTPError,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.otp_token))
            },
            onValueChange = {
                viewModel.otpTokenTextState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        if (viewModel.showOTPError) {
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.please_enter_otp_token),
                style = MaterialTheme.typography.caption.copy(color = Red300),
            )
        }
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.test_email_magic_link_flow),
            onClick = { viewModel.loginOrCreate() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.test_otp_sms_flow),
            onClick = { viewModel.loginOrCreateSMS() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.test_otp_whatsapp_flow),
            onClick = { viewModel.loginOrCreateWhatsApp() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.test_otp_email_flow),
            onClick = { viewModel.loginOrCreateEmail() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.test_otp_authenticate_flow),
            onClick = { viewModel.authenticateOTP() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.revoke_session),
            onClick = { viewModel.revokeSession() },
        )
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            Text(text = responseState.value, modifier = Modifier.padding(8.dp))
        }
    }
}

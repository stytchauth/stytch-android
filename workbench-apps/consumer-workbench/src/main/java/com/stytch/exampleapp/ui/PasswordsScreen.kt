package com.stytch.exampleapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.stytch.exampleapp.PasswordsViewModel
import com.stytch.exampleapp.R
import com.stytch.exampleapp.theme.Red300

@Composable
fun PasswordsScreen(navController: NavController) {
    val viewModel: PasswordsViewModel = viewModel()
    val loading = viewModel.loadingState.collectAsState()
    val responseState = viewModel.currentResponse.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.emailTextState,
            isError = viewModel.showEmailError,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.stytch_email_label))
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
            value = viewModel.passwordTextState,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.stytch_password_label))
            },
            onValueChange = {
                viewModel.passwordTextState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.newPasswordTextState,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.passwords_new))
            },
            onValueChange = {
                viewModel.newPasswordTextState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.tokenTextState,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.token))
            },
            onValueChange = {
                viewModel.tokenTextState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.check_password_strength),
            onClick = { viewModel.checkPassword() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.authenticate),
            onClick = { viewModel.authenticate() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passwords_create),
            onClick = { viewModel.createAccount() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passwords_reset_by_email_start),
            onClick = { viewModel.resetPasswordByEmailStart() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passwords_reset_by_email),
            onClick = { viewModel.resetPasswordByEmail() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passwords_reset_by_session),
            onClick = { viewModel.resetPasswordBySession() },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passwords_reset_by_existing),
            onClick = viewModel::resetByExisting,
        )
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            Text(text = responseState.value, modifier = Modifier.padding(8.dp))
        }
    }
}

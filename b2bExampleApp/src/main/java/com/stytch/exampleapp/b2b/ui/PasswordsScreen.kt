package com.stytch.exampleapp.b2b.ui

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
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.PasswordsViewModel
import com.stytch.exampleapp.b2b.R

@Composable
fun PasswordsScreen(
    viewModel: PasswordsViewModel
) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(1F, false),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.emailState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
                onValueChange = {
                    viewModel.emailState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize)
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.existingPasswordState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.existing_password))
                },
                onValueChange = {
                    viewModel.existingPasswordState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize)
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.newPasswordState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.new_password))
                },
                onValueChange = {
                    viewModel.newPasswordState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize)
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_resetByEmail),
                onClick = viewModel::resetByEmailStart
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_resetByExisting),
                onClick = viewModel::resetByExisting
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_resetBySession),
                onClick = viewModel::resetBySession
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_authenticate),
                onClick = viewModel::authenticate
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.button_strengthCheck),
                onClick = viewModel::strengthCheck
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(0.5F, false),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                Text(text = responseState.value, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

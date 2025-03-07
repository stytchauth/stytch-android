package com.stytch.exampleapp.ui

import androidx.activity.compose.LocalActivity
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.stytch.exampleapp.PasskeysViewModel
import com.stytch.exampleapp.R

@Composable
fun PasskeysScreen(navController: NavController) {
    val viewModel: PasskeysViewModel = viewModel()
    val loading = viewModel.loadingState.collectAsState()
    val responseState = viewModel.currentResponse.collectAsState()
    val activity = LocalActivity.current as FragmentActivity
    val registrationId = viewModel.currentPasskeyRegistrationId.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passkeys_register),
            onClick = { viewModel.registerPasskey(activity) },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passkeys_authenticate),
            onClick = { viewModel.authenticatePasskey(activity) },
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.passkeys_clear),
            onClick = viewModel::clearPasskeyRegistrations,
        )
        if (registrationId.value.isNotBlank()) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.passkeyNameState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.passkey_name))
                },
                onValueChange = {
                    viewModel.passkeyNameState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.passkey_update),
                onClick = viewModel::updatePasskey,
            )
        }
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            Text(text = responseState.value, modifier = Modifier.padding(8.dp))
        }
    }
}

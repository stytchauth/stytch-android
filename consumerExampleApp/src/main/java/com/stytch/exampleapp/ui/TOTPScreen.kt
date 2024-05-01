package com.stytch.exampleapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stytch.exampleapp.R
import com.stytch.exampleapp.TOTPViewModel

@Composable
fun TOTPScreen() {
    val viewModel: TOTPViewModel = viewModel()
    val loading = viewModel.loadingState.collectAsState()
    val responseState = viewModel.currentResponse.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.totpCodeState,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.totp_code))
            },
            onValueChange = {
                viewModel.totpCodeState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.totpRecoveryCodeState,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.totp_recovery_code))
            },
            onValueChange = {
                viewModel.totpRecoveryCodeState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.totp_create),
            onClick = viewModel::create,
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.totp_authenticate),
            onClick = viewModel::authenticate,
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.totp_recovery_codes),
            onClick = viewModel::recoveryCodes,
        )
        StytchButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.totp_recover),
            onClick = viewModel::recover,
        )
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            SelectionContainer {
                Text(text = responseState.value, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

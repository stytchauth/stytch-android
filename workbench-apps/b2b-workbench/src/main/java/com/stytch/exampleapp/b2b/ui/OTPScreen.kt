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
import com.stytch.exampleapp.b2b.OTPViewModel
import com.stytch.exampleapp.b2b.R

@Composable
fun OTPScreen(viewModel: OTPViewModel) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(1F, false),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.orgIdState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.b2b_org_id))
                },
                onValueChange = {
                    viewModel.orgIdState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.memberIdState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.member_id_label))
                },
                onValueChange = {
                    viewModel.memberIdState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.phoneNumberState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.member_phone))
                },
                onValueChange = {
                    viewModel.phoneNumberState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.memberEmailState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.member_email))
                },
                onValueChange = {
                    viewModel.memberEmailState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.codeState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.code))
                },
                onValueChange = {
                    viewModel.codeState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.otp_sms_send),
                onClick = viewModel::send,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.otp_sms_authenticate),
                onClick = viewModel::authenticate,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.otp_email_loginOrCreate),
                onClick = viewModel::emailLoginOrCreate,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.otp_email_authenticate),
                onClick = viewModel::emailAuthenticate,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.otp_email_discovery_send),
                onClick = viewModel::emailDiscoverySend,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.otp_email_discovery_authenticate),
                onClick = viewModel::emailDiscoveryAuthenticate,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(1F, false),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                Text(text = responseState.value, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

package com.stytch.exampleapp.b2b.ui

import androidx.activity.compose.LocalActivity
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
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.b2b.OAuthViewModel
import com.stytch.exampleapp.b2b.R

@Composable
fun OAuthScreen(viewModel: OAuthViewModel) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    val context = LocalActivity.current as FragmentActivity
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
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_start),
                onClick = { viewModel.startGoogleOAuthFlow(context) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_discovery_start),
                onClick = { viewModel.startGoogleDiscoveryOAuthFlow(context) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_start_oneshot),
                onClick = { viewModel.startGoogleOauthFlowOneShot() },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_discovery_start_oneshot),
                onClick = { viewModel.startGoogleDiscoveryOauthFlowOneShot() },
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

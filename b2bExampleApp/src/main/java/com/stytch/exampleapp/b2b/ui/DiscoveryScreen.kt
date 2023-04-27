package com.stytch.exampleapp.b2b.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.DiscoveryViewModel
import com.stytch.exampleapp.b2b.R

@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel,
    intermediateSessionToken: String = ""
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
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.discovery_discover),
                onClick = viewModel::organizations
            )

            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.create_organization),
                onClick = { viewModel.createOrganization(intermediateSessionToken) }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(1F, false),
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

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
import com.stytch.exampleapp.b2b.R
import com.stytch.exampleapp.b2b.SCIMViewModel

@Composable
fun ScimScreen(viewModel: SCIMViewModel) {
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
                value = viewModel.connectionIdState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.scim_connection_id))
                },
                onValueChange = {
                    viewModel.connectionIdState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.displayNameState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.scim_display_name))
                },
                onValueChange = {
                    viewModel.displayNameState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.identityProviderState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.scim_identity_provider))
                },
                onValueChange = {
                    viewModel.identityProviderState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_create),
                onClick = viewModel::createConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_update),
                onClick = viewModel::updateConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_delete),
                onClick = viewModel::deleteConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_get),
                onClick = viewModel::getConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_get_groups),
                onClick = viewModel::getConnectionGroups,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_rotate_start),
                onClick = viewModel::rotateStart,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_rotate_cancel),
                onClick = viewModel::rotateCancel,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.scim_rotate_complete),
                onClick = viewModel::rotateComplete,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(0.5F, false),
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

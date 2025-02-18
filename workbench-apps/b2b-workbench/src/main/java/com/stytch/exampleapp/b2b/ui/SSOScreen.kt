package com.stytch.exampleapp.b2b.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.selection.SelectionContainer
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
import com.stytch.exampleapp.b2b.R
import com.stytch.exampleapp.b2b.SSOViewModel

@Composable
fun SSOScreen(viewModel: SSOViewModel) {
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
                value = viewModel.ssoConnectionId,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.sso_connection_id))
                },
                onValueChange = {
                    viewModel.ssoConnectionId = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.metadataUrl,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.sso_metadata_url))
                },
                onValueChange = {
                    viewModel.metadataUrl = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.certificateId,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.sso_certificate_id))
                },
                onValueChange = {
                    viewModel.certificateId = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_start),
                onClick = { viewModel.startSSO(context) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_start_oneshot),
                onClick = { viewModel.startSSOOneShot() },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_get_connections),
                onClick = viewModel::getConnections,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_delete_connection),
                onClick = viewModel::deleteConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_saml_create),
                onClick = viewModel::createSamlConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_saml_update),
                onClick = viewModel::updateSamlConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_saml_update_url),
                onClick = viewModel::updateSamlConnectionByUrl,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_saml_delete_certificate),
                onClick = viewModel::deleteVerificationCertificate,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_oidc_create),
                onClick = viewModel::createOidcConnection,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.sso_oidc_update),
                onClick = viewModel::updateOidcConnection,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(0.5F, false),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                SelectionContainer {
                    Text(text = responseState.value, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

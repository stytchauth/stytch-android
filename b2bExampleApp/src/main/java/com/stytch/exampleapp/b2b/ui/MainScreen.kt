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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.b2b.HomeViewModel
import com.stytch.exampleapp.b2b.R
import com.stytch.exampleapp.b2b.theme.Red300

@Composable
fun MainScreen(viewModel: HomeViewModel) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1F, false),
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
                value = viewModel.orgSlugState,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.organization_slug))
                },
                onValueChange = {
                    viewModel.orgSlugState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize),
            )
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.emailState,
                isError = viewModel.showEmailError,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
                onValueChange = {
                    viewModel.emailState = it
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
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.b2b_org_details),
                onClick = { viewModel.getOrganizationDetails() },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.test_email_magic_link_flow),
                onClick = { viewModel.sendEmailMagicLink() },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.send_discovery_magic_link),
                onClick = viewModel::sendDiscoveryMagicLink,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.send_invite_magic_link),
                onClick = viewModel::sendInviteMagicLink,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.exchange_session),
                onClick = viewModel::exchangeSession,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.revoke_session),
                onClick = { viewModel.revokeSession() },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.organization_search),
                onClick = viewModel::searchOrganizationBySlug,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.organization_member_search),
                onClick = viewModel::searchOrganizationMembers,
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.get_pkce_code_pair),
                onClick = viewModel::getPKCECodePair,
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .weight(1F, false),
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

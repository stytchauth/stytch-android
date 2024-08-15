package com.stytch.exampleapp.ui

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.OAuthProvider
import com.stytch.exampleapp.OAuthViewModel
import com.stytch.exampleapp.R

@Composable
fun OAuthScreen(viewModel: OAuthViewModel) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    val context = LocalContext.current as FragmentActivity
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).weight(1F, false),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_apple),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.APPLE) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_amazon),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.AMAZON) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_bitbucket),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.BITBUCKET) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_coinbase),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.COINBASE) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_discord),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.DISCORD) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_facebook),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.FACEBOOK) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_google_onetap),
                onClick = { viewModel.loginWithGoogleOneTap(context) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_google_thirdparty),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.GOOGLE) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_github),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.GITHUB) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_gitlab),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.GITLAB) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_linkedin),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.LINKEDIN) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_microsoft),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.MICROSOFT) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_salesforce),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.SALESFORCE) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_slack),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.SLACK) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_twitch),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.TWITCH) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.oauth_twitch),
                onClick = { viewModel.loginWithThirdPartyOAuth(context, OAuthProvider.YAHOO) },
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

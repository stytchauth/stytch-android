package com.stytch.exampleapp.ui.headless.oauth

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState

@Composable
fun OAuthScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = OAuthScreenViewModel(reportState)
    val context = LocalActivity.current as FragmentActivity
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = { viewModel.loginWithGoogleOneTap(context) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Google One Tap")
        }
        OAuthProvider.entries.map {
            OutlinedButton(
                onClick = { viewModel.loginWithThirdPartyOAuth(it) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(it.name)
            }
        }
    }
}

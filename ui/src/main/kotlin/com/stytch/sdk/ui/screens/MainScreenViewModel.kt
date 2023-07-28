package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_GOOGLE_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.data.GoogleOAuthOptions
import com.stytch.sdk.ui.data.OAuthOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
internal class MainScreenViewModel : ViewModel() {
    internal fun onStartGoogle(
        context: ComponentActivity,
        googleOAuthOptions: GoogleOAuthOptions? = null,
        oAuthOptions: OAuthOptions? = null
    ) {
        viewModelScope.launch {
            val didStartOneTap = googleOAuthOptions?.clientId?.let { clientId ->
                StytchClient.oauth.googleOneTap.start(
                    OAuth.GoogleOneTap.StartParameters(
                        context = context,
                        clientId = clientId,
                        oAuthRequestIdentifier = STYTCH_GOOGLE_OAUTH_REQUEST_ID,
                    ),
                )
            } ?: false
            if (!didStartOneTap) {
                // Google OneTap is unavailable, fallback to traditional OAuth
                StytchClient.oauth.google.start(
                    OAuth.ThirdParty.StartParameters(
                        context = context,
                        oAuthRequestIdentifier = STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID,
                        loginRedirectUrl = oAuthOptions?.loginRedirectURL,
                        signupRedirectUrl = oAuthOptions?.signupRedirectURL,
                    ),
                )
            }
        }
    }
    internal fun onStartApple(context: ComponentActivity, oAuthOptions: OAuthOptions? = null) {
        StytchClient.oauth.apple.start(
            OAuth.ThirdParty.StartParameters(
                context = context,
                oAuthRequestIdentifier = STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID,
                loginRedirectUrl = oAuthOptions?.loginRedirectURL,
                signupRedirectUrl = oAuthOptions?.signupRedirectURL,
            ),
        )
    }
}

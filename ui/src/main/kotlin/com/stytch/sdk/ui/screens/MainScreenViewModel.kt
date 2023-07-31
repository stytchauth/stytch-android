package com.stytch.sdk.ui.screens

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_GOOGLE_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.AuthenticationActivity.Companion.STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID
import com.stytch.sdk.ui.data.OAuthOptions
import com.stytch.sdk.ui.data.OAuthProvider
import com.stytch.sdk.ui.data.StytchProductConfig
import kotlinx.coroutines.launch

internal class MainScreenViewModel : ViewModel() {
    internal fun onStartOAuthLogin(
        context: ComponentActivity,
        provider: OAuthProvider,
        productConfig: StytchProductConfig
    ) {
        if (provider == OAuthProvider.GOOGLE) {
            viewModelScope.launch {
                val didStartOneTap = productConfig.googleOauthOptions?.clientId?.let { clientId ->
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
                    onStartThirdParty(context, provider = provider, oAuthOptions = productConfig.oAuthOptions)
                }
            }
        } else {
            onStartThirdParty(context, provider = provider, oAuthOptions = productConfig.oAuthOptions)
        }
    }

    internal fun onStartThirdParty(
        context: ComponentActivity,
        provider: OAuthProvider,
        oAuthOptions: OAuthOptions? = null
    ) {
        val parameters = OAuth.ThirdParty.StartParameters(
            context = context,
            oAuthRequestIdentifier = STYTCH_THIRD_PARTY_OAUTH_REQUEST_ID,
            loginRedirectUrl = oAuthOptions?.loginRedirectURL,
            signupRedirectUrl = oAuthOptions?.signupRedirectURL,
        )
        when (provider) {
            OAuthProvider.APPLE -> StytchClient.oauth.apple.start(parameters)
            OAuthProvider.GOOGLE -> StytchClient.oauth.google.start(parameters)
        }
    }
}

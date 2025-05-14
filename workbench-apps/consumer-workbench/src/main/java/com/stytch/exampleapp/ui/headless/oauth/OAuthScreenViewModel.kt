package com.stytch.exampleapp.ui.headless.oauth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.oauth.OAuth
import kotlinx.coroutines.launch

enum class OAuthProvider {
    APPLE,
    AMAZON,
    BITBUCKET,
    COINBASE,
    DISCORD,
    FACEBOOK,
    GOOGLE,
    GITHUB,
    GITLAB,
    LINKEDIN,
    MICROSOFT,
    SALESFORCE,
    SLACK,
    TWITCH,
    YAHOO,
}

class OAuthScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun loginWithGoogleOneTap(
        context: Activity,
        googleOauthClientId: String,
    ) {
        viewModelScope
            .launch {
                reportState(HeadlessMethodResponseState.Loading)
                reportState(
                    HeadlessMethodResponseState.Response(
                        StytchClient.oauth.googleOneTap
                            .start(
                                OAuth.GoogleOneTap.StartParameters(
                                    context = context,
                                    clientId = googleOauthClientId,
                                ),
                            ),
                    ),
                )
            }
    }

    fun loginWithThirdPartyOAuth(provider: OAuthProvider) {
        val startParameters =
            OAuth.ThirdParty.GetTokenForProviderParams(
                loginRedirectUrl = "app://consumerworkbench?type=login",
                signupRedirectUrl = "app://consumerworkbench?type=signup",
            )
        viewModelScope
            .launch {
                reportState(HeadlessMethodResponseState.Loading)
                val result =
                    when (provider) {
                        OAuthProvider.APPLE -> StytchClient.oauth.apple.getTokenForProvider(startParameters)
                        OAuthProvider.AMAZON -> StytchClient.oauth.amazon.getTokenForProvider(startParameters)
                        OAuthProvider.BITBUCKET -> StytchClient.oauth.bitbucket.getTokenForProvider(startParameters)
                        OAuthProvider.COINBASE -> StytchClient.oauth.coinbase.getTokenForProvider(startParameters)
                        OAuthProvider.DISCORD -> StytchClient.oauth.discord.getTokenForProvider(startParameters)
                        OAuthProvider.FACEBOOK -> StytchClient.oauth.facebook.getTokenForProvider(startParameters)
                        OAuthProvider.GOOGLE -> StytchClient.oauth.google.getTokenForProvider(startParameters)
                        OAuthProvider.GITHUB -> StytchClient.oauth.github.getTokenForProvider(startParameters)
                        OAuthProvider.GITLAB -> StytchClient.oauth.gitlab.getTokenForProvider(startParameters)
                        OAuthProvider.LINKEDIN -> StytchClient.oauth.linkedin.getTokenForProvider(startParameters)
                        OAuthProvider.MICROSOFT -> StytchClient.oauth.microsoft.getTokenForProvider(startParameters)
                        OAuthProvider.SALESFORCE -> StytchClient.oauth.salesforce.getTokenForProvider(startParameters)
                        OAuthProvider.SLACK -> StytchClient.oauth.slack.getTokenForProvider(startParameters)
                        OAuthProvider.TWITCH -> StytchClient.oauth.twitch.getTokenForProvider(startParameters)
                        OAuthProvider.YAHOO -> StytchClient.oauth.yahoo.getTokenForProvider(startParameters)
                    }
                reportState(HeadlessMethodResponseState.Response(result))
                when (result) {
                    is StytchResult.Success -> {
                        reportState(
                            HeadlessMethodResponseState.Response(
                                StytchClient.oauth
                                    .authenticate(
                                        OAuth.ThirdParty.AuthenticateParameters(
                                            token = result.value,
                                            sessionDurationMinutes = 30,
                                        ),
                                    ),
                            ),
                        )
                    }
                    is StytchResult.Error -> reportState(HeadlessMethodResponseState.Response(result))
                }
            }
    }
}

package com.stytch.exampleapp

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sso.SSOError
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.oauth.OAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

class OAuthViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun loginWithGoogleOneTap(context: Activity) {
        viewModelScope
            .launch {
                _currentResponse.value =
                    StytchClient.oauth.googleOneTap
                        .start(
                            OAuth.GoogleOneTap.StartParameters(
                                context = context,
                                clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID,
                            ),
                        ).toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun loginWithThirdPartyOAuth(
        context: Activity,
        provider: OAuthProvider,
    ) {
        val startParameters =
            OAuth.ThirdParty.StartParameters(
                context = context,
                oAuthRequestIdentifier = THIRD_PARTY_OAUTH_REQUEST,
                loginRedirectUrl = "app://consumerworkbench/?type={}",
                signupRedirectUrl = "app://consumerworkbench/?type={}",
            )
        when (provider) {
            OAuthProvider.APPLE -> StytchClient.oauth.apple.start(startParameters)
            OAuthProvider.AMAZON -> StytchClient.oauth.amazon.start(startParameters)
            OAuthProvider.BITBUCKET -> StytchClient.oauth.bitbucket.start(startParameters)
            OAuthProvider.COINBASE -> StytchClient.oauth.coinbase.start(startParameters)
            OAuthProvider.DISCORD -> StytchClient.oauth.discord.start(startParameters)
            OAuthProvider.FACEBOOK -> StytchClient.oauth.facebook.start(startParameters)
            OAuthProvider.GOOGLE -> StytchClient.oauth.google.start(startParameters)
            OAuthProvider.GITHUB -> StytchClient.oauth.github.start(startParameters)
            OAuthProvider.GITLAB -> StytchClient.oauth.gitlab.start(startParameters)
            OAuthProvider.LINKEDIN -> StytchClient.oauth.linkedin.start(startParameters)
            OAuthProvider.MICROSOFT -> StytchClient.oauth.microsoft.start(startParameters)
            OAuthProvider.SALESFORCE -> StytchClient.oauth.salesforce.start(startParameters)
            OAuthProvider.SLACK -> StytchClient.oauth.slack.start(startParameters)
            OAuthProvider.TWITCH -> StytchClient.oauth.twitch.start(startParameters)
            OAuthProvider.YAHOO -> StytchClient.oauth.yahoo.start(startParameters)
        }
    }

    fun loginWithThirdPartyOAuthOneShot(provider: OAuthProvider) {
        val startParameters =
            OAuth.ThirdParty.GetTokenForProviderParams(
                loginRedirectUrl = "app://consumerworkbench/?type={}",
                signupRedirectUrl = "app://consumerworkbench/?type={}",
            )
        viewModelScope
            .launch {
                _loadingState.value = true
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
                _currentResponse.value =
                    when (result) {
                        is StytchResult.Success -> {
                            StytchClient.oauth
                                .authenticate(
                                    OAuth.ThirdParty.AuthenticateParameters(
                                        token = result.value,
                                        sessionDurationMinutes = 30,
                                    ),
                                ).toFriendlyDisplay()
                        }
                        is StytchResult.Error -> {
                            result.toFriendlyDisplay()
                        }
                    }
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun authenticateThirdPartyOAuth(
        resultCode: Int,
        intent: Intent,
    ) {
        viewModelScope
            .launch {
                _loadingState.value = true
                if (resultCode == RESULT_OK) {
                    intent.data?.let {
                        val result = StytchClient.handle(it, 60)
                        _currentResponse.value =
                            when (result) {
                                is DeeplinkHandledStatus.NotHandled -> result.reason.message
                                is DeeplinkHandledStatus.Handled -> result.response.result.toFriendlyDisplay()
                                // This only happens for password reset deeplinks
                                is DeeplinkHandledStatus.ManualHandlingRequired -> ""
                            }
                    }
                } else {
                    intent.extras?.getSerializable(SSOError.SSO_EXCEPTION)?.let {
                        when (it as SSOError) {
                            is SSOError.UserCanceled -> {} // do nothing
                            is SSOError.NoBrowserFound,
                            is SSOError.NoURIFound,
                            -> _currentResponse.value = it.message
                        }
                    }
                }
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }
}

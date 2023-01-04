package com.stytch.exampleapp

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.OAuth
import com.stytch.sdk.StytchClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class OAuthProvider {
    GOOGLE,
    GITHUB,
}

class OAuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun loginWithGoogleOneTap(context: Activity) {
        viewModelScope.launch {
            val didStart = StytchClient.oauth.googleOneTap.start(
                OAuth.GoogleOneTap.StartParameters(
                    context = context,
                    clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID,
                    oAuthRequestIdentifier = GOOGLE_OAUTH_REQUEST
                )
            )
            _currentResponse.value = if (didStart) "Starting Google OneTap" else "Google OneTap not available"
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun authenticateGoogleOneTapLogin(data: Intent) {
        viewModelScope.launch {
            _currentResponse.value = "Authenticating Google OneTap login"
            val result = StytchClient.oauth.googleOneTap.authenticate(OAuth.GoogleOneTap.AuthenticateParameters(data))
            _currentResponse.value = result.toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun loginWithThirdPartyOAuth(context: Context, provider: OAuthProvider) {
        val startParameters = OAuth.ThirdParty.StartParameters(
            context = context,
            loginRedirectUrl = "app://exampleapp.com/oauth",
            signupRedirectUrl = "app://exampleapp.com/oauth",
        )
        when (provider) {
            OAuthProvider.GOOGLE -> StytchClient.oauth.google.start(startParameters)
            OAuthProvider.GITHUB -> StytchClient.oauth.github.start(startParameters)
        }
    }
}

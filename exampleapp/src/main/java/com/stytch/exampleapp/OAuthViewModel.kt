package com.stytch.exampleapp

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.OAuth
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OAuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun loginWithGoogle(context: Activity) {
        viewModelScope.launch {
            val didStart = StytchClient.oauth.google.start(
                OAuth.Google.StartParameters(
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

    fun authenticateGoogleLogin(data: Intent) {
        viewModelScope.launch {
            _currentResponse.value = "Authenticating Google OneTap login"
            val result = StytchClient.oauth.google.authenticate(OAuth.Google.AuthenticateParameters(data))
            _currentResponse.value = when (result) {
                is StytchResult.Success<*> -> result.toString()
                is StytchResult.Error -> result.exception.reason?.toString() ?: "Unknown exception"
            }
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }
}

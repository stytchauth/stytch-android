package com.stytch.sdk.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.oauth.OAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal sealed class AuthenticationState {
    object Idle : AuthenticationState()
    data class Result(val response: StytchResult<*>) : AuthenticationState()
}

internal class AuthenticationViewModel : ViewModel() {
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
    val state = _state.asStateFlow()

    fun test() {
        _state.value = AuthenticationState.Result(StytchResult.Error(StytchExceptions.Input("test")))
    }

    fun authenticateGoogleOneTapLogin(data: Intent) {
        viewModelScope.launch {
            val parameters = OAuth.GoogleOneTap.AuthenticateParameters(data)
            val result = StytchClient.oauth.googleOneTap.authenticate(parameters)
            _state.value = AuthenticationState.Result(result)
        }
    }

    fun authenticateThirdPartyOAuth(resultCode: Int, intent: Intent) {
        viewModelScope.launch {
            /*
            if (resultCode == Activity.RESULT_OK) {
                intent.data?.let {
                    when (val result = StytchClient.handle(it, 60U)) {
                        is DeeplinkHandledStatus.NotHandled -> _authenticationError.value = result.reason
                        is DeeplinkHandledStatus.Handled -> {
                            _authenticationError.value = null
                            _navigationState.value = NavigationState.Navigate("profile")
                        }
                        // This only happens for password reset deeplinks
                        is DeeplinkHandledStatus.ManualHandlingRequired -> {}
                    }
                }
            } else {
                intent.extras?.getSerializable(OAuthError.OAUTH_EXCEPTION)?.let {
                    when (it as OAuthError) {
                        is OAuthError.UserCanceled -> {
                            _authenticationError.value = null
                        } // do nothing
                        is OAuthError.NoBrowserFound,
                        is OAuthError.NoURIFound,
                        -> _authenticationError.value = it.message
                    }
                }
            }
             */
        }
    }
}

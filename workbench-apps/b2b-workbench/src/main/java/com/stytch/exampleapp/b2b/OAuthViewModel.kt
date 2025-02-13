package com.stytch.exampleapp.b2b

import android.app.Activity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.oauth.OAuth
import com.stytch.sdk.common.StytchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OAuthViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var orgIdState by mutableStateOf(TextFieldValue(""))

    fun startGoogleOAuthFlow(context: Activity) {
        viewModelScope.launchAndToggleLoadingState {
            StytchB2BClient.oauth.google.start(
                OAuth.Provider.StartParameters(
                    context = context,
                    oAuthRequestIdentifier = B2B_OAUTH_REQUEST,
                    organizationId = orgIdState.text,
                    loginRedirectUrl = "app://b2bworkbench?type={}",
                    signupRedirectUrl = "app://b2bworkbench?type={}",
                ),
            )
        }
    }

    fun startGoogleDiscoveryOAuthFlow(context: Activity) {
        viewModelScope.launchAndToggleLoadingState {
            StytchB2BClient.oauth.google.discovery.start(
                OAuth.ProviderDiscovery.DiscoveryStartParameters(
                    context = context,
                    oAuthRequestIdentifier = B2B_OAUTH_REQUEST,
                    discoveryRedirectUrl = "app://b2bworkbench?type={}",
                ),
            )
        }
    }

    fun startGoogleOauthFlowOneShot() {
        viewModelScope.launchAndToggleLoadingState {
            val result =
                StytchB2BClient.oauth.google.getTokenForProvider(
                    OAuth.Provider.GetTokenForProviderParams(
                        organizationId = orgIdState.text,
                        loginRedirectUrl = "app://b2bOAuth",
                        signupRedirectUrl = "app://b2bOAuth",
                    ),
                )
            _currentResponse.value =
                when (result) {
                    is StytchResult.Success -> {
                        StytchB2BClient.oauth
                            .authenticate(
                                OAuth.AuthenticateParameters(
                                    oauthToken = result.value,
                                    sessionDurationMinutes = 30,
                                ),
                            ).toFriendlyDisplay()
                    }
                    is StytchResult.Error -> {
                        result.toFriendlyDisplay()
                    }
                }
        }
    }

    fun startGoogleDiscoveryOauthFlowOneShot() {
        viewModelScope.launchAndToggleLoadingState {
            val result =
                StytchB2BClient.oauth.google.discovery.getTokenForProvider(
                    OAuth.ProviderDiscovery.GetTokenForProviderParams(
                        discoveryRedirectUrl = "app://b2bOAuth",
                    ),
                )
            _currentResponse.value =
                when (result) {
                    is StytchResult.Success -> {
                        StytchB2BClient.oauth.discovery
                            .authenticate(
                                OAuth.Discovery.DiscoveryAuthenticateParameters(
                                    discoveryOauthToken = result.value,
                                ),
                            ).toFriendlyDisplay()
                    }
                    is StytchResult.Error -> {
                        result.toFriendlyDisplay()
                    }
                }
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle =
        launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
}

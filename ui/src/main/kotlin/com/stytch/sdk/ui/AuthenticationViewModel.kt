package com.stytch.sdk.ui

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.sso.SSOError
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.ui.data.AuthenticationState
import com.stytch.sdk.ui.data.SessionOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class AuthenticationViewModel : ViewModel() {
    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
    val state = _state.asStateFlow()

    fun authenticateGoogleOneTapLogin(data: Intent, sessionOptions: SessionOptions) {
        viewModelScope.launch {
            val parameters = OAuth.GoogleOneTap.AuthenticateParameters(
                data = data,
                sessionDurationMinutes = sessionOptions.sessionDurationMinutes
            )
            val result = StytchClient.oauth.googleOneTap.authenticate(parameters)
            _state.value = AuthenticationState.Result(result)
        }
    }

    fun authenticateThirdPartyOAuth(resultCode: Int, intent: Intent, sessionOptions: SessionOptions) {
        viewModelScope.launch {
            if (resultCode == Activity.RESULT_OK) {
                intent.data?.let {
                    when (val result = StytchClient.handle(it, sessionOptions.sessionDurationMinutes)) {
                        is DeeplinkHandledStatus.Handled -> {
                            _state.value = AuthenticationState.Result(result.response.result)
                        }
                        is DeeplinkHandledStatus.NotHandled -> {} // TODO: report to app
                        is DeeplinkHandledStatus.ManualHandlingRequired -> {} // TODO: Navigate to password reset
                    }
                }
            } else {
                intent.extras?.getSerializable(SSOError.SSO_EXCEPTION)?.let {
                    _state.value = AuthenticationState.Result(
                        StytchResult.Error(
                            StytchExceptions.Input(
                                when (it as SSOError) {
                                    is SSOError.UserCanceled -> "User Cancelled"
                                    is SSOError.NoBrowserFound,
                                    is SSOError.NoURIFound -> it.message
                                }
                            )
                        )
                    )
                }
            }
        }
    }
}

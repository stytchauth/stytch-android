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
import com.stytch.sdk.ui.data.OAuthAuthenticationResult
import com.stytch.sdk.ui.data.SessionOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class AuthenticationViewModel : ViewModel() {
    private val _state = MutableSharedFlow<OAuthAuthenticationResult>()
    val state = _state.asSharedFlow()

    fun authenticateGoogleOneTapLogin(data: Intent, sessionOptions: SessionOptions) {
        viewModelScope.launch {
            val parameters = OAuth.GoogleOneTap.AuthenticateParameters(
                data = data,
                sessionDurationMinutes = sessionOptions.sessionDurationMinutes
            )
            val result = StytchClient.oauth.googleOneTap.authenticate(parameters)
            _state.emit(OAuthAuthenticationResult(result))
        }
    }

    fun authenticateThirdPartyOAuth(resultCode: Int, intent: Intent, sessionOptions: SessionOptions) {
        viewModelScope.launch {
            if (resultCode == Activity.RESULT_OK) {
                intent.data?.let {
                    when (val result = StytchClient.handle(it, sessionOptions.sessionDurationMinutes)) {
                        is DeeplinkHandledStatus.Handled -> {
                            _state.emit(OAuthAuthenticationResult(result.response.result))
                        }
                        else -> {} // this shouldn't happen
                    }
                }
            } else {
                intent.extras?.getSerializable(SSOError.SSO_EXCEPTION)?.let {
                    _state.emit(
                        OAuthAuthenticationResult(StytchResult.Error(StytchExceptions.Critical(it as SSOError)))
                    )
                }
            }
        }
    }
}

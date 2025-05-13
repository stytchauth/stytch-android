package com.stytch.sdk.ui.b2c

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchSSOError
import com.stytch.sdk.common.sso.SSOError
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import com.stytch.sdk.ui.b2c.data.NavigationRoute
import com.stytch.sdk.ui.shared.data.SessionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.collections.set

internal class AuthenticationViewModel(
    private val stytchClient: StytchClient,
    val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    fun enableBiometricRegistrationOnAuthentication(value: Boolean) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] =
            uiState.value.copy(showBiometricRegistrationOnLogin = value)
    }

    fun authenticateThirdPartyOAuth(
        resultCode: Int,
        intent: Intent,
        sessionOptions: SessionOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            if (resultCode == Activity.RESULT_OK) {
                intent.data?.let {
                    when (val result = stytchClient.handle(it, sessionOptions.sessionDurationMinutes)) {
                        is DeeplinkHandledStatus.Handled -> {
                            _eventFlow.emit(EventState.Authenticated(result.response.result))
                        }
                        else -> {} // this shouldn't happen
                    }
                }
            } else {
                intent.extras?.getSerializable(SSOError.SSO_EXCEPTION)?.let {
                    _eventFlow.emit(
                        EventState.Authenticated(StytchResult.Error(StytchSSOError(it as SSOError))),
                    )
                }
            }
        }
    }

    fun handleDeepLink(
        uri: Uri,
        sessionOptions: SessionOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            when (
                val result =
                    stytchClient.handle(
                        uri = uri,
                        sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
                    )
            ) {
                is DeeplinkHandledStatus.Handled -> {
                    _eventFlow.emit(EventState.Authenticated(result.response.result))
                }
                is DeeplinkHandledStatus.NotHandled -> {
                    _eventFlow.emit(EventState.Exit)
                }
                is DeeplinkHandledStatus.ManualHandlingRequired -> {
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.SetNewPassword(token = result.token),
                        ),
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    AuthenticationViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

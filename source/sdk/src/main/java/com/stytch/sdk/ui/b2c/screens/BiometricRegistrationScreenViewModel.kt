package com.stytch.sdk.ui.b2c.screens

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.Biometrics
import com.stytch.sdk.ui.b2c.data.ApplicationUIState
import com.stytch.sdk.ui.b2c.data.EventState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class BiometricRegistrationScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun registerBiometrics(context: FragmentActivity) {
        viewModelScope.launch {
            val result =
                stytchClient.biometrics.register(
                    Biometrics.RegisterParameters(context = context),
                )
            _eventFlow.emit(EventState.Authenticated(result))
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    BiometricRegistrationScreenViewModel(
                        stytchClient = StytchClient,
                        savedStateHandle = savedStateHandle,
                    )
                }
            }
    }
}

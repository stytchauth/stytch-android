package com.stytch.exampleapp

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.Biometrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BiometricsViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun registerBiometrics(context: FragmentActivity, promptData: Biometrics.PromptData? = null) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.biometrics.register(
                Biometrics.RegisterParameters(
                    context = context,
                    promptData = promptData,
                    allowFallbackToCleartext = false,
                    allowDeviceCredentials = true,
                )
            )
            _currentResponse.value = result.toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun authenticateBiometrics(context: FragmentActivity, promptData: Biometrics.PromptData? = null) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.biometrics.authenticate(
                Biometrics.AuthenticateParameters(context = context, promptData = promptData)
            )
            _currentResponse.value = result.toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun removeRegistration() {
        viewModelScope.launch {
            val deleted = StytchClient.biometrics.removeRegistration()
            _currentResponse.value = "Registration removed: $deleted"
        }
    }
}

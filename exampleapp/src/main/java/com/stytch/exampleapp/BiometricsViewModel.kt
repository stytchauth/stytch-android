package com.stytch.exampleapp

import android.app.Application
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.Biometrics
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchResult
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

    fun showBiometricsError(error: String) {
        _currentResponse.value = error
    }

    fun registerBiometrics(context: FragmentActivity, promptInfo: PromptInfo? = null) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.biometrics.register(
                Biometrics.RegisterParameters(
                    context = context,
                    promptInfo = promptInfo,
                    allowFallbackToCleartext = false,
                    showBiometricPrompt = false,
                    failOnExistingRegistration = true
                )
            )
            _currentResponse.value = when (result) {
                is StytchResult.Success<*> -> result.toString()
                is StytchResult.Error -> result.exception.reason?.toString() ?: "Unknown exception"
            }
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun authenticateBiometrics(context: FragmentActivity, promptInfo: PromptInfo? = null) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.biometrics.authenticate(
                Biometrics.AuthenticateParameters(context = context, promptInfo = promptInfo)
            )
            _currentResponse.value = when (result) {
                is StytchResult.Success<*> -> result.toString()
                is StytchResult.Error -> result.exception.reason?.toString() ?: "Unknown exception"
            }
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun removeRegistration() {
        _currentResponse.value = "Registration removed: ${StytchClient.biometrics.removeRegistration()}"
    }
}

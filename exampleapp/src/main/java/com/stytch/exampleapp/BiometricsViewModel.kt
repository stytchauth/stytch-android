package com.stytch.exampleapp

import android.app.Application
import android.content.Context
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

    fun registerBiometrics(context: Context) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.biometrics.register(
                Biometrics.StartParameters(context = context)
            )
            _currentResponse.value = when (result) {
                is StytchResult.Success<*> -> result.toString()
                is StytchResult.Error -> result.exception.reason?.toString() ?: "Unknown exception"
            }
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun authenticateBiometrics(context: Context) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.biometrics.authenticate(
                Biometrics.StartParameters(context = context)
            )
            _currentResponse.value = when (result) {
                is StytchResult.Success<*> -> result.toString()
                is StytchResult.Error -> result.exception.reason?.toString() ?: "Unknown exception"
            }
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }
}

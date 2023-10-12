package com.stytch.exampleapp

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passkeys.Passkeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasskeysViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun registerPasskey(context: FragmentActivity) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.passkeys.register(
                Passkeys.RegisterParameters(
                    context = context,
                )
            )
            _currentResponse.value = result.toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun authenticatePasskey(context: FragmentActivity) {
        viewModelScope.launch {
            _loadingState.value = true
            val result = StytchClient.passkeys.authenticate(
                Passkeys.AuthenticateParameters(context = context)
            )
            _currentResponse.value = result.toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }
}

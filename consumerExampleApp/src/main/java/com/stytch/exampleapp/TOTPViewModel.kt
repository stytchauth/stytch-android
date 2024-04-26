package com.stytch.exampleapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.totp.TOTP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TOTPViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var totpCodeState by mutableStateOf(TextFieldValue(""))
    var totpRecoveryCodeState by mutableStateOf(TextFieldValue(""))

    fun create() {
        viewModelScope.launch {
            _loadingState.value = true
            _currentResponse.value =
                StytchClient.totp.create(
                    TOTP.CreateParameters(5),
                ).toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun authenticate() {
        viewModelScope.launch {
            _loadingState.value = true
            _currentResponse.value =
                StytchClient.totp.authenticate(
                    TOTP.AuthenticateParameters(totpCode = totpCodeState.text),
                ).toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun recoveryCodes() {
        viewModelScope.launch {
            _loadingState.value = true
            _currentResponse.value =
                StytchClient.totp.recoveryCodes().toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }

    fun recover() {
        viewModelScope.launch {
            _loadingState.value = true
            _currentResponse.value =
                StytchClient.totp.recover(
                    TOTP.RecoverParameters(recoveryCode = totpRecoveryCodeState.text),
                ).toFriendlyDisplay()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }
}

package com.stytch.exampleapp.b2b

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.recoveryCodes.RecoveryCodes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecoveryCodesViewModel : ViewModel() {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var orgIdState by mutableStateOf(TextFieldValue(""))
    var memberIdState by mutableStateOf(TextFieldValue(""))
    var codeState by mutableStateOf(TextFieldValue(""))

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle =
        launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }

    fun get() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value = StytchB2BClient.recoveryCodes.get().toFriendlyDisplay()
        }
    }

    fun rotate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value = StytchB2BClient.recoveryCodes.rotate().toFriendlyDisplay()
        }
    }

    fun recover() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.recoveryCodes
                    .recover(
                        RecoveryCodes.RecoverParameters(
                            organizationId = orgIdState.text,
                            memberId = memberIdState.text,
                            sessionDurationMinutes = 30,
                            recoveryCode = codeState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }
}

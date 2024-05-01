package com.stytch.exampleapp.b2b

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.b2b.totp.TOTP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TOTPViewModel : ViewModel() {
    var orgIdState by mutableStateOf(TextFieldValue(BuildConfig.STYTCH_B2B_ORG_ID))
    var memberIdState by mutableStateOf(TextFieldValue(""))
    var codeState by mutableStateOf(TextFieldValue(""))
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun create() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.totp.create(
                    TOTP.CreateParameters(
                        organizationId = orgIdState.text,
                        memberId = memberIdState.text,
                    ),
                ).toFriendlyDisplay()
        }
    }

    fun authenticate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.totp.authenticate(
                    TOTP.AuthenticateParameters(
                        organizationId = orgIdState.text,
                        memberId = memberIdState.text,
                        code = codeState.text,
                        setMFAEnrollment = SetMFAEnrollment.ENROLL,
                        sessionDurationMinutes = 30U,
                    ),
                ).toFriendlyDisplay()
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle {
        return launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
    }
}

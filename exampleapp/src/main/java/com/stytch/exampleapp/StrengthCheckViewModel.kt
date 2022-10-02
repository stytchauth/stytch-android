package com.stytch.exampleapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.Passwords
import com.stytch.sdk.StytchClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StrengthCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    var emailTextState by mutableStateOf(TextFieldValue(""))
    var passwordTextState by mutableStateOf(TextFieldValue(""))

    val emailIsValid
        get() = isValidEmail(emailTextState.text)

    var showEmailError by mutableStateOf(false)

    fun checkPassword() {
        if (emailIsValid) {
            viewModelScope.launch {
                _loadingState.value = true
                val result = StytchClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(
                        emailTextState.text,
                        passwordTextState.text
                    )
                )
                _currentResponse.value = result.toString()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
        } else {
            showEmailError = true
        }
    }
}

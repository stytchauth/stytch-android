package com.stytch.exampleapp.b2b

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.passwords.Passwords
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasswordsViewModel : ViewModel() {
    var emailState by mutableStateOf(TextFieldValue(""))
    var existingPasswordState by mutableStateOf(TextFieldValue(""))
    var newPasswordState by mutableStateOf(TextFieldValue(""))
    var passwordResetTokenState by mutableStateOf(TextFieldValue(""))

    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    fun resetByEmailStart() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords
                    .resetByEmailStart(
                        Passwords.ResetByEmailStartParameters(
                            organizationId = BuildConfig.STYTCH_B2B_ORG_ID,
                            emailAddress = emailState.text,
                            loginRedirectUrl = "app://b2bexampleapp.com/",
                            resetPasswordRedirectUrl = "app://b2bexampleapp.com/",
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun resetByExisting() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords
                    .resetByExisting(
                        Passwords.ResetByExistingPasswordParameters(
                            organizationId = BuildConfig.STYTCH_B2B_ORG_ID,
                            emailAddress = emailState.text,
                            existingPassword = existingPasswordState.text,
                            newPassword = newPasswordState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun resetBySession() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords
                    .resetBySession(
                        Passwords.ResetBySessionParameters(
                            organizationId = BuildConfig.STYTCH_B2B_ORG_ID,
                            password = newPasswordState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun authenticate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords
                    .authenticate(
                        Passwords.AuthParameters(
                            organizationId = BuildConfig.STYTCH_B2B_ORG_ID,
                            emailAddress = emailState.text,
                            password = existingPasswordState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun strengthCheck() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords
                    .strengthCheck(
                        Passwords.StrengthCheckParameters(
                            email = emailState.text,
                            password = newPasswordState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun discoveryResetByEmailStart() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords.discovery
                    .resetByEmailStart(
                        Passwords.Discovery.ResetByEmailStartParameters(
                            emailAddress = emailState.text,
                            resetPasswordRedirectUrl = "app://b2bexampleapp.com",
                            discoveryRedirectUrl = "app://b2bexampleapp.com",
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun discoveryResetByEmail() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords.discovery
                    .resetByEmail(
                        Passwords.Discovery.ResetByEmailParameters(
                            passwordResetToken = passwordResetTokenState.text,
                            password = newPasswordState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    fun discoveryAuthenticate() {
        viewModelScope.launchAndToggleLoadingState {
            _currentResponse.value =
                StytchB2BClient.passwords.discovery
                    .authenticate(
                        Passwords.Discovery.AuthenticateParameters(
                            emailAddress = emailState.text,
                            password = existingPasswordState.text,
                        ),
                    ).toFriendlyDisplay()
        }
    }

    private fun CoroutineScope.launchAndToggleLoadingState(block: suspend () -> Unit): DisposableHandle =
        launch {
            _loadingState.value = true
            block()
        }.invokeOnCompletion {
            _loadingState.value = false
        }
}

package com.stytch.exampleapp.ui.headless.passwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.coroutines.launch

class PasswordsScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun create(
        emailAddress: String,
        password: String,
    ) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(
                    StytchClient.passwords.create(Passwords.CreateParameters(emailAddress, password)),
                ),
            )
        }
    }

    fun authenticate(
        emailAddress: String,
        password: String,
    ) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(
                    StytchClient.passwords.authenticate(Passwords.AuthParameters(emailAddress, password)),
                ),
            )
        }
    }

    fun strengthCheck(
        emailAddress: String,
        password: String,
    ) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(
                    StytchClient.passwords.strengthCheck(Passwords.StrengthCheckParameters(emailAddress, password)),
                ),
            )
        }
    }

    fun resetByExisting(
        emailAddress: String,
        existingPassword: String,
        newPassword: String,
    ) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(
                    StytchClient.passwords.resetByExistingPassword(
                        Passwords.ResetByExistingPasswordParameters(
                            email = emailAddress,
                            existingPassword = existingPassword,
                            newPassword = newPassword,
                        ),
                    ),
                ),
            )
        }
    }

    fun resetBySession(newPassword: String) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(
                    StytchClient.passwords.resetBySession(Passwords.ResetBySessionParameters(newPassword)),
                ),
            )
        }
    }
}

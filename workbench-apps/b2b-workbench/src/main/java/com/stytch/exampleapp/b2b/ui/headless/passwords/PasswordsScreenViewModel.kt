package com.stytch.exampleapp.b2b.ui.headless.passwords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.passwords.Passwords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PasswordsScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: PasswordsAction) =
        when (action) {
            is PasswordsAction.Authenticate -> authenticate(action.organizationId, action.emailAddress, action.password)
            is PasswordsAction.DiscoveryAuthenticate -> discoveryAuthenticate(action.emailAddress, action.password)
            is PasswordsAction.DiscoveryResetByEmailStart -> discoveryResetByEmailStart(action.emailAddress)
            is PasswordsAction.ResetByEmailStart -> resetByEmailStart(action.organizationId, action.emailAddress)
            is PasswordsAction.ResetByExistingPassword ->
                resetByExistingPassword(
                    action.organizationId,
                    action.emailAddress,
                    action.existingPassword,
                    action.newPassword,
                )
            is PasswordsAction.ResetBySession -> resetBySession(action.organizationId, action.newPassword)
            is PasswordsAction.StrengthCheck -> strengthCheck(action.emailAddress, action.password)
        }

    private fun authenticate(
        organizationId: String,
        emailAddress: String,
        password: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.passwords.authenticate(
                    Passwords.AuthParameters(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                        password = password,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun discoveryAuthenticate(
        emailAddress: String,
        password: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.passwords.discovery.authenticate(
                    Passwords.Discovery.AuthenticateParameters(
                        emailAddress = emailAddress,
                        password = password,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun discoveryResetByEmailStart(emailAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.passwords.discovery.resetByEmailStart(
                    Passwords.Discovery.ResetByEmailStartParameters(
                        emailAddress = emailAddress,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun resetByEmailStart(
        organizationId: String,
        emailAddress: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.passwords.resetByEmailStart(
                    Passwords.ResetByEmailStartParameters(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun resetByExistingPassword(
        organizationId: String,
        emailAddress: String,
        existingPassword: String,
        newPassword: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.passwords.resetByExisting(
                    Passwords.ResetByExistingPasswordParameters(
                        organizationId = organizationId,
                        emailAddress = emailAddress,
                        existingPassword = existingPassword,
                        newPassword = newPassword,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun resetBySession(
        organizationId: String,
        newPassword: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.passwords.resetBySession(
                    Passwords.ResetBySessionParameters(
                        organizationId = organizationId,
                        password = newPassword,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun strengthCheck(
        emailAddress: String,
        password: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(
                        email = emailAddress,
                        password = password,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface PasswordsAction {
    data class ResetByExistingPassword(
        val organizationId: String,
        val emailAddress: String,
        val existingPassword: String,
        val newPassword: String,
    ) : PasswordsAction

    data class ResetBySession(
        val organizationId: String,
        val newPassword: String,
    ) : PasswordsAction

    data class ResetByEmailStart(
        val organizationId: String,
        val emailAddress: String,
    ) : PasswordsAction

    data class Authenticate(
        val organizationId: String,
        val emailAddress: String,
        val password: String,
    ) : PasswordsAction

    data class StrengthCheck(
        val emailAddress: String,
        val password: String,
    ) : PasswordsAction

    data class DiscoveryResetByEmailStart(
        val emailAddress: String,
    ) : PasswordsAction

    data class DiscoveryAuthenticate(
        val emailAddress: String,
        val password: String,
    ) : PasswordsAction
}

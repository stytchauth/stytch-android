package com.stytch.exampleapp.ui.headless.totp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.totp.TOTP
import kotlinx.coroutines.launch

class TOTPScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun create() {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchClient.totp.create(TOTP.CreateParameters(5))))
        }
    }

    fun authenticate(totp: String) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(StytchClient.totp.authenticate(TOTP.AuthenticateParameters(totp))),
            )
        }
    }

    fun getRecoveryCodes() {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(HeadlessMethodResponseState.Response(StytchClient.totp.recoveryCodes()))
        }
    }

    fun useRecoveryCode(recoveryCode: String) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(StytchClient.totp.recover(TOTP.RecoverParameters(recoveryCode))),
            )
        }
    }
}

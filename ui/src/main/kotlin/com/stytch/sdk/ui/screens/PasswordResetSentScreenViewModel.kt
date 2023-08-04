package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class PasswordResetUiState(
    val showResendDialog: Boolean = false,
    val genericErrorMessage: String? = null
)
internal class PasswordResetSentScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordResetUiState())
    val uiState = _uiState.asStateFlow()

    fun onDialogDismiss() {
        _uiState.value = _uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        _uiState.value = _uiState.value.copy(showResendDialog = true)
    }

    fun onResendPasswordResetStart(parameters: Passwords.ResetByEmailStartParameters) {
        onDialogDismiss()
        viewModelScope.launch {
            when (val result = StytchClient.passwords.resetByEmailStart(parameters = parameters)) {
                is StytchResult.Success -> {} // do nothing
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        genericErrorMessage = result.exception.reason.toString() // TODO
                    )
                }
            }
        }
    }
}

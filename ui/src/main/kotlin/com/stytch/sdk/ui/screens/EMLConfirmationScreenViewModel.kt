package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class EMLConfirmationUiState(
    val showResendDialog: Boolean = false,
    val genericErrorMessage: String? = null,
)

internal class EMLConfirmationScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EMLConfirmationUiState())
    val uiState = _uiState.asStateFlow()

    fun resendEML(parameters: MagicLinks.EmailMagicLinks.Parameters) {
        viewModelScope.launch {
            when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        showResendDialog = false,
                        genericErrorMessage = null,
                    )
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        showResendDialog = false,
                        genericErrorMessage = result.exception.reason.toString() // TODO
                    )
                }
            }
        }
    }

    fun onDialogDismiss() {
        _uiState.value = _uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        _uiState.value = _uiState.value.copy(showResendDialog = true)
    }
}

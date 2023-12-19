package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class EMLConfirmationUiState(
    val showResendDialog: Boolean = false,
    val genericErrorMessage: String? = null,
) : Parcelable

internal class EMLConfirmationScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    val stytchClient: StytchClient = StytchClient
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow("EMLConfirmationUiState", EMLConfirmationUiState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun resendEML(parameters: MagicLinks.EmailMagicLinks.Parameters, scope: CoroutineScope = viewModelScope) {
        scope.launch {
            when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["EMLConfirmationUiState"] = uiState.value.copy(
                        showResendDialog = false,
                        genericErrorMessage = null,
                    )
                }
                is StytchResult.Error -> {
                    savedStateHandle["EMLConfirmationUiState"] = uiState.value.copy(
                        showResendDialog = false,
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            }
        }
    }

    fun onDialogDismiss() {
        savedStateHandle["EMLConfirmationUiState"] = uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        savedStateHandle["EMLConfirmationUiState"] = uiState.value.copy(showResendDialog = true)
    }

    fun sendResetPasswordEmail(
        emailAddress: String?,
        passwordOptions: PasswordOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            emailAddress?.let {
                val parameters = passwordOptions.toResetByEmailStartParameters(emailAddress)
                when (val result = stytchClient.passwords.resetByEmailStart(parameters)) {
                    is StytchResult.Success -> _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.PasswordResetSent(
                                PasswordResetDetails(parameters, PasswordResetType.NO_PASSWORD_SET),
                            ),
                        ),
                    )
                    is StytchResult.Error -> savedStateHandle["EMLConfirmationUiState"] = uiState.value.copy(
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            } ?: run {
                // this should never happen
                savedStateHandle["EMLConfirmationUiState"] = uiState.value.copy(
                    genericErrorMessage = "Can't reset password for unknown email address",
                )
            }
        }
    }
}

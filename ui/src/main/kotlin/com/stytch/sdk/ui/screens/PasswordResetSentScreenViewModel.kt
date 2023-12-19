package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordResetUiState(
    val showResendDialog: Boolean = false,
    val genericErrorMessage: String? = null,
    val showLoadingDialog: Boolean = false,
) : Parcelable

internal class PasswordResetSentScreenViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow("PasswordResetUiState", PasswordResetUiState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onDialogDismiss() {
        savedStateHandle["PasswordResetUiState"] = uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        savedStateHandle["PasswordResetUiState"] = uiState.value.copy(showResendDialog = true)
    }

    fun onResendPasswordResetStart(parameters: Passwords.ResetByEmailStartParameters) {
        onDialogDismiss()
        viewModelScope.launch {
            when (val result = StytchClient.passwords.resetByEmailStart(parameters = parameters)) {
                is StytchResult.Success -> {} // do nothing
                is StytchResult.Error -> {
                    savedStateHandle["PasswordResetUiState"] = uiState.value.copy(
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            }
        }
    }
    fun sendEML(emailAddress: String, emailMagicLinksOptions: EmailMagicLinksOptions) {
        savedStateHandle["PasswordResetUiState"] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = emailMagicLinksOptions.toParameters(emailAddress)
            when (val result = StytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["PasswordResetUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(
                                details = EMLDetails(parameters),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> savedStateHandle["PasswordResetUiState"] = uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.message, // TODO
                )
            }
        }
    }

    fun sendEmailOTP(emailAddress: String, otpOptions: OTPOptions) {
        savedStateHandle["PasswordResetUiState"] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        viewModelScope.launch {
            val parameters = otpOptions.toEmailOtpParameters(emailAddress)
            when (val result = StytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle["PasswordResetUiState"] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                details = OTPDetails.EmailOTP(parameters, methodId = result.value.methodId),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> savedStateHandle["PasswordResetUiState"] = uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.message, // TODO
                )
            }
        }
    }
}

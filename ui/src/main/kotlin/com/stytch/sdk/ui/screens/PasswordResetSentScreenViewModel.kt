package com.stytch.sdk.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EMLDetails
import com.stytch.sdk.ui.data.EmailMagicLinksOptions
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.OTPOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class PasswordResetSentScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onDialogDismiss() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = true)
    }

    fun onResendPasswordResetStart(
        parameters: Passwords.ResetByEmailStartParameters,
        scope: CoroutineScope = viewModelScope,
    ) {
        onDialogDismiss()
        scope.launch {
            when (val result = stytchClient.passwords.resetByEmailStart(parameters = parameters)) {
                is StytchResult.Success -> {} // do nothing
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            }
        }
    }
    fun sendEML(
        emailAddress: String,
        emailMagicLinksOptions: EmailMagicLinksOptions,
        scope: CoroutineScope = viewModelScope
    ) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        scope.launch {
            val parameters = emailMagicLinksOptions.toParameters(
                emailAddress = emailAddress,
                publicToken = stytchClient.publicToken,
            )
            when (val result = stytchClient.magicLinks.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.EMLConfirmation(
                                details = EMLDetails(parameters),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.message, // TODO
                )
            }
        }
    }

    fun sendEmailOTP(emailAddress: String, otpOptions: OTPOptions, scope: CoroutineScope = viewModelScope) {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
            showLoadingDialog = true,
            genericErrorMessage = null,
        )
        scope.launch {
            val parameters = otpOptions.toEmailOtpParameters(emailAddress)
            when (val result = stytchClient.otps.email.loginOrCreate(parameters)) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showLoadingDialog = false)
                    _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.OTPConfirmation(
                                details = OTPDetails.EmailOTP(parameters, methodId = result.value.methodId),
                                isReturningUser = true,
                            ),
                        ),
                    )
                }
                is StytchResult.Error -> savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                    showLoadingDialog = false,
                    genericErrorMessage = result.exception.message, // TODO
                )
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PasswordResetSentScreenViewModel(
                    stytchClient = StytchClient,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}

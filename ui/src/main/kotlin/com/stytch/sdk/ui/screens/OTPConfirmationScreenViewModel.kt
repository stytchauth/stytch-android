package com.stytch.sdk.ui.screens

import android.text.format.DateUtils
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.data.ApplicationUIState
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import com.stytch.sdk.ui.data.SessionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class OTPConfirmationScreenViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val stytchClient: StytchClient,
) : ViewModel() {
    val uiState = savedStateHandle.getStateFlow(ApplicationUIState.SAVED_STATE_KEY, ApplicationUIState())

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    @VisibleForTesting
    internal var methodId: String = ""

    @VisibleForTesting
    internal var resendCountdownSeconds: Long = 0
    @VisibleForTesting
    internal var countdownSeconds: Long = 0
        set(value) {
            field = value
            savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                expirationTimeFormatted = DateUtils.formatElapsedTime(value),
            )
        }
    fun setInitialState(resendParameters: OTPDetails, scope: CoroutineScope = viewModelScope) {
        methodId = when (resendParameters) {
            is OTPDetails.EmailOTP -> resendParameters.methodId
            is OTPDetails.SmsOTP -> resendParameters.methodId
            is OTPDetails.WhatsAppOTP -> resendParameters.methodId
        }
        countdownSeconds = (
            when (resendParameters) {
                is OTPDetails.EmailOTP -> resendParameters.parameters.expirationMinutes
                is OTPDetails.SmsOTP -> resendParameters.parameters.expirationMinutes
                is OTPDetails.WhatsAppOTP -> resendParameters.parameters.expirationMinutes
            } * 60U
            ).toLong()
        resendCountdownSeconds = countdownSeconds
        scope.launch {
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds -= 1
            }
        }
    }

    fun onDialogDismiss() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(showResendDialog = true)
    }

    fun authenticateOTP(token: String, sessionOptions: SessionOptions, scope: CoroutineScope = viewModelScope) {
        scope.launch {
            when (
                val result = stytchClient.otps.authenticate(
                    OTP.AuthParameters(
                        token = token,
                        methodId = methodId,
                        sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
                    ),
                )
            ) {
                is StytchResult.Success -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        showLoadingDialog = false,
                        genericErrorMessage = null,
                    )
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        showLoadingDialog = false,
                        genericErrorMessage = result.exception.message,
                    )
                }
            }
        }
    }

    fun resendOTP(resend: OTPDetails, scope: CoroutineScope = viewModelScope) {
        scope.launch {
            val result = when (resend) {
                is OTPDetails.EmailOTP -> stytchClient.otps.email.loginOrCreate(resend.parameters)
                is OTPDetails.SmsOTP -> stytchClient.otps.sms.loginOrCreate(resend.parameters)
                is OTPDetails.WhatsAppOTP -> stytchClient.otps.whatsapp.loginOrCreate(resend.parameters)
            }
            when (result) {
                is StytchResult.Success -> {
                    methodId = result.value.methodId
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        showLoadingDialog = false,
                        showResendDialog = false,
                    )
                    countdownSeconds = resendCountdownSeconds
                }
                is StytchResult.Error -> {
                    savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        showLoadingDialog = false,
                        showResendDialog = false,
                        genericErrorMessage = result.exception.message,
                    )
                }
            }
        }
    }

    fun sendResetPasswordEmail(
        emailAddress: String?,
        passwordOptions: PasswordOptions,
        scope: CoroutineScope = viewModelScope,
    ) {
        scope.launch {
            emailAddress?.let {
                val parameters = passwordOptions.toResetByEmailStartParameters(
                    emailAddress = emailAddress,
                    publicToken = stytchClient.publicToken,
                )
                when (val result = stytchClient.passwords.resetByEmailStart(parameters)) {
                    is StytchResult.Success -> _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.PasswordResetSent(
                                PasswordResetDetails(parameters, PasswordResetType.NO_PASSWORD_SET),
                            ),
                        ),
                    )
                    is StytchResult.Error -> savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                        genericErrorMessage = result.exception.message, // TODO
                    )
                }
            } ?: run {
                // this should never happen
                savedStateHandle[ApplicationUIState.SAVED_STATE_KEY] = uiState.value.copy(
                    genericErrorMessage = "Can't reset password for unknown email address",
                )
            }
        }
    }

    companion object {
        fun factory(savedStateHandle: SavedStateHandle): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                OTPConfirmationScreenViewModel(
                    stytchClient = StytchClient,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}

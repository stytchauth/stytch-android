package com.stytch.sdk.ui.screens

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.SessionOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class OTPConfirmationUiState(
    val expirationTimeFormatted: String = "",
    val showLoadingOverlay: Boolean = false,
    val showResendDialog: Boolean = false,
    val genericErrorMessage: String? = null,
)

internal data class AuthenticatedState(val result: StytchResult<Any>)

internal class OTPConfirmationScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OTPConfirmationUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AuthenticatedState>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var methodId: String = ""
    private var resendCountdownSeconds: Long = 0
    private var countdownSeconds: Long = 0
        set(value) {
            field = value
            _uiState.value = _uiState.value.copy(
                expirationTimeFormatted = DateUtils.formatElapsedTime(value)
            )
        }
    private var didInitialize = false
    fun setInitialState(resendParameters: OTPDetails) {
        if (didInitialize) return
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
        viewModelScope.launch {
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds -= 1
            }
        }
        didInitialize = true
    }

    fun onDialogDismiss() {
        _uiState.value = _uiState.value.copy(showResendDialog = false)
    }

    fun onShowResendDialog() {
        _uiState.value = _uiState.value.copy(showResendDialog = true)
    }

    fun authenticateOTP(token: String, sessionOptions: SessionOptions) {
        viewModelScope.launch {
            when (
                val result = StytchClient.otps.authenticate(
                    OTP.AuthParameters(
                        token = token,
                        methodId = methodId,
                        sessionDurationMinutes = sessionOptions.sessionDurationMinutes,
                    )
                )
            ) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingOverlay = false,
                        genericErrorMessage = null,
                    )
                    _eventFlow.emit(AuthenticatedState(result))
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingOverlay = false,
                        genericErrorMessage = result.exception.reason.toString(),
                    )
                }
            }
        }
    }

    fun resendOTP(resend: OTPDetails) {
        viewModelScope.launch {
            val result = when (resend) {
                is OTPDetails.EmailOTP -> StytchClient.otps.email.loginOrCreate(resend.parameters)
                is OTPDetails.SmsOTP -> StytchClient.otps.sms.loginOrCreate(resend.parameters)
                is OTPDetails.WhatsAppOTP -> StytchClient.otps.whatsapp.loginOrCreate(resend.parameters)
            }
            when (result) {
                is StytchResult.Success -> {
                    methodId = result.value.methodId
                    _uiState.value = _uiState.value.copy(
                        showLoadingOverlay = false,
                        showResendDialog = false,
                    )
                    countdownSeconds = resendCountdownSeconds
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingOverlay = false,
                        showResendDialog = false,
                        genericErrorMessage = result.exception.reason.toString(),
                    )
                }
            }
        }
    }
}

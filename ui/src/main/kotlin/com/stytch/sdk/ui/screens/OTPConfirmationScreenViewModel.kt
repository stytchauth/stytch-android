package com.stytch.sdk.ui.screens

import android.text.format.DateUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.data.EventState
import com.stytch.sdk.ui.data.NavigationRoute
import com.stytch.sdk.ui.data.OTPDetails
import com.stytch.sdk.ui.data.PasswordOptions
import com.stytch.sdk.ui.data.PasswordResetDetails
import com.stytch.sdk.ui.data.PasswordResetType
import com.stytch.sdk.ui.data.SessionOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal data class OTPConfirmationUiState(
    val expirationTimeFormatted: String = "",
    val showLoadingDialog: Boolean = false,
    val showResendDialog: Boolean = false,
    val genericErrorMessage: String? = null,
)

internal class OTPConfirmationScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OTPConfirmationUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<EventState>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var methodId: String = ""
    private var resendCountdownSeconds: Long = 0
    private var countdownSeconds: Long = 0
        set(value) {
            field = value
            _uiState.value = _uiState.value.copy(
                expirationTimeFormatted = DateUtils.formatElapsedTime(value),
            )
        }
    fun setInitialState(resendParameters: OTPDetails) {
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
                    ),
                )
            ) {
                is StytchResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingDialog = false,
                        genericErrorMessage = null,
                    )
                    _eventFlow.emit(EventState.Authenticated(result))
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingDialog = false,
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
                        showLoadingDialog = false,
                        showResendDialog = false,
                    )
                    countdownSeconds = resendCountdownSeconds
                }
                is StytchResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        showLoadingDialog = false,
                        showResendDialog = false,
                        genericErrorMessage = result.exception.reason.toString(),
                    )
                }
            }
        }
    }

    fun sendResetPasswordEmail(emailAddress: String?, passwordOptions: PasswordOptions) {
        viewModelScope.launch {
            emailAddress?.let {
                val parameters = passwordOptions.toResetByEmailStartParameters(emailAddress)
                when (val result = StytchClient.passwords.resetByEmailStart(parameters)) {
                    is StytchResult.Success -> _eventFlow.emit(
                        EventState.NavigationRequested(
                            NavigationRoute.PasswordResetSent(
                                PasswordResetDetails(parameters, PasswordResetType.NO_PASSWORD_SET),
                            ),
                        ),
                    )
                    is StytchResult.Error -> _uiState.value = _uiState.value.copy(
                        genericErrorMessage = result.exception.reason.toString(), // TODO
                    )
                }
            } ?: run {
                // this should never happen
                _uiState.value = _uiState.value.copy(
                    genericErrorMessage = "Can't reset password for unknown email address",
                )
            }
        }
    }
}

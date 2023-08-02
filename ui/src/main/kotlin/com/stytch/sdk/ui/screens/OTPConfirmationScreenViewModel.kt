package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.data.SessionOptions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal sealed class ConfirmationState {
    object Idle : ConfirmationState()

    data class Confirmed(val result: StytchResult<Any>) : ConfirmationState()

    data class Failed(val message: String) : ConfirmationState()
}
internal class OTPConfirmationScreenViewModel : ViewModel() {
    private val _didResend = MutableSharedFlow<String>(0)
    val didResend = _didResend.asSharedFlow()

    private val _confirmationState = MutableStateFlow<ConfirmationState>(ConfirmationState.Idle)
    val confirmationState = _confirmationState.asStateFlow()

    fun authenticateOTP(token: String, methodId: String, sessionOptions: SessionOptions) {
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
                is StytchResult.Success -> _confirmationState.emit(
                    ConfirmationState.Confirmed(result)
                )
                is StytchResult.Error -> _confirmationState.emit(
                    ConfirmationState.Failed(
                        result.exception.reason.toString()
                    )
                )
            }
        }
    }

    fun resendOTP(resend: OTPPersistence) {
        viewModelScope.launch {
            val result = when (resend) {
                is OTPPersistence.EmailOTP -> StytchClient.otps.email.loginOrCreate(resend.parameters)
                is OTPPersistence.SmsOTP -> StytchClient.otps.sms.loginOrCreate(resend.parameters)
                is OTPPersistence.WhatsAppOTP -> StytchClient.otps.whatsapp.loginOrCreate(resend.parameters)
            }
            when (result) {
                is StytchResult.Success -> _didResend.emit(result.value.methodId)
                is StytchResult.Error -> {
                    _confirmationState.value = ConfirmationState.Failed(result.exception.reason.toString())
                }
            }
        }
    }
}

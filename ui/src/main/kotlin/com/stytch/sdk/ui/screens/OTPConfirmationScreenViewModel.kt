package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal sealed class ConfirmationState {
    object Idle : ConfirmationState()

    object Confirmed : ConfirmationState()

    data class Failed(val message: String) : ConfirmationState()
}
internal class OTPConfirmationScreenViewModel : ViewModel() {
    private val _didResend = MutableSharedFlow<Boolean>(0)
    val didResend = _didResend.asSharedFlow()

    private val _confirmationState = MutableStateFlow<ConfirmationState>(ConfirmationState.Idle)
    val confirmationState = _confirmationState.asStateFlow()

    private var methodId: String = ""

    fun setInitialMethodId(methodId: String) {
        this.methodId = methodId
    }

    fun authenticateOTP(token: String) {
        viewModelScope.launch {
            when (
                val result = StytchClient.otps.authenticate(
                    OTP.AuthParameters(
                        token = token,
                        methodId = methodId,
                    )
                )
            ) {
                is StytchResult.Success -> _confirmationState.emit(ConfirmationState.Confirmed)
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
                is StytchResult.Success -> {
                    methodId = result.value.methodId
                    _didResend.emit(true)
                }
                is StytchResult.Error -> {
                    _confirmationState.value = ConfirmationState.Failed(result.exception.reason.toString())
                }
            }
        }
    }
}

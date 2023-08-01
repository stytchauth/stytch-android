package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
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

    fun resendOTP(resend: OTPResendParameters) {
        viewModelScope.launch {
            val result = when (resend) {
                is OTPResendParameters.EmailOTP -> StytchClient.otps.email.loginOrCreate(resend.parameters)
                is OTPResendParameters.SmsOTP -> StytchClient.otps.sms.loginOrCreate(resend.parameters)
                is OTPResendParameters.WhatsAppOTP -> StytchClient.otps.whatsapp.loginOrCreate(resend.parameters)
            }
            when (result) {
                is StytchResult.Success -> _didResend.emit(true)
                is StytchResult.Error -> {
                    _confirmationState.value = ConfirmationState.Failed(result.exception.reason.toString())
                }
            }
        }
    }
}

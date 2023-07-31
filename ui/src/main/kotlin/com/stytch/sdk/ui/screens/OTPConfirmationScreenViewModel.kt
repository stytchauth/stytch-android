package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.ui.data.OTPMethods
import kotlinx.coroutines.launch

internal class OTPConfirmationScreenViewModel : ViewModel() {
    fun resendOTP(recipient: String, method: OTPMethods) {
        viewModelScope.launch {
            when (method) {
                OTPMethods.EMAIL ->
                    StytchClient.otps.email.loginOrCreate(OTP.EmailOTP.Parameters(email = recipient))
                OTPMethods.SMS ->
                    StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters(phoneNumber = recipient))
                OTPMethods.WHATSAPP ->
                    StytchClient.otps.whatsapp.loginOrCreate(OTP.WhatsAppOTP.Parameters(phoneNumber = recipient))
            }
        }
    }
}

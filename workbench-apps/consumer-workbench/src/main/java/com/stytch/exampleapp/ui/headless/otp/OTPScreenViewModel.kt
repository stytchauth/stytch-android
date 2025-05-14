package com.stytch.exampleapp.ui.headless.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.otp.OTP
import kotlinx.coroutines.launch

enum class OTPType {
    EMAIL,
    SMS,
    WHATSAPP,
}

class OTPScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    private var methodId: String? = null

    fun sendOtp(
        destination: String,
        type: OTPType,
    ) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            val result =
                when (type) {
                    OTPType.EMAIL ->
                        StytchClient.otps.email.loginOrCreate(
                            OTP.EmailOTP.Parameters(email = destination),
                        )
                    OTPType.SMS ->
                        StytchClient.otps.sms.loginOrCreate(
                            OTP.SmsOTP.Parameters(phoneNumber = destination),
                        )
                    OTPType.WHATSAPP ->
                        StytchClient.otps.whatsapp.loginOrCreate(
                            OTP.WhatsAppOTP.Parameters(phoneNumber = destination),
                        )
                }
            reportState(HeadlessMethodResponseState.Response(result))
            when (result) {
                is StytchResult.Success -> {
                    methodId = result.value.methodId
                }
                else -> {}
            }
        }
    }

    fun authenticateOtp(code: String) {
        methodId?.let {
            viewModelScope.launch {
                reportState(HeadlessMethodResponseState.Loading)
                reportState(
                    HeadlessMethodResponseState.Response(
                        StytchClient.otps.authenticate(
                            OTP.AuthParameters(
                                token = code,
                                methodId = it,
                            ),
                        ),
                    ),
                )
            }
        }
    }
}

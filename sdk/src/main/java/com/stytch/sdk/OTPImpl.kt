package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sessions.launchSessionUpdater
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OTPImpl internal constructor() : OTP {

    override val sms: OTP.SmsOTP = SmsOTPImpl()
    override val whatsapp: OTP.WhatsAppOTP = WhatsAppOTPImpl()
    override val email: OTP.EmailOTP = EmailOTPImpl()

    override suspend fun authenticate(parameters: OTP.AuthParameters): AuthResponse {
        val result: AuthResponse
        withContext(StytchClient.ioDispatcher) {
            // call backend endpoint
            result = StytchApi.OTP.authenticateWithOTP(
                token = parameters.token,
                methodId = parameters.methodId,
                sessionDurationMinutes = parameters.sessionDurationMinutes
            ).apply {
                launchSessionUpdater()
            }
        }
        return result
    }

    override fun authenticate(parameters: OTP.AuthParameters, callback: (response: AuthResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    private inner class SmsOTPImpl : OTP.SmsOTP {
        override suspend fun loginOrCreate(parameters: OTP.SmsOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.OTP.loginOrCreateByOTPWithSMS(
                    phoneNumber = parameters.phoneNumber,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(parameters: OTP.SmsOTP.Parameters, callback: (response: LoginOrCreateOTPResponse) -> Unit) {
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

    }

    private inner class WhatsAppOTPImpl : OTP.WhatsAppOTP {
        override suspend fun loginOrCreate(parameters: OTP.WhatsAppOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.OTP.loginOrCreateUserByOTPWithWhatsApp(
                    phoneNumber = parameters.phoneNumber,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(parameters: OTP.WhatsAppOTP.Parameters, callback: (response: LoginOrCreateOTPResponse) -> Unit) {
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

    }

    private inner class EmailOTPImpl : OTP.EmailOTP {
        override suspend fun loginOrCreate(parameters: OTP.EmailOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.OTP.loginOrCreateUserByOTPWithEmail(
                    email = parameters.email,
                    expirationMinutes = parameters.expirationMinutes
                )
            }

            return result
        }

        override fun loginOrCreate(parameters: OTP.EmailOTP.Parameters, callback: (response: LoginOrCreateOTPResponse) -> Unit) {
            GlobalScope.launch(StytchClient.uiDispatcher) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

    }
}

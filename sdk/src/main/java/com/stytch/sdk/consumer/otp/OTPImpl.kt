package com.stytch.sdk.consumer.otp

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.LoginOrCreateOTPResponse
import com.stytch.sdk.consumer.OTPSendResponse
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OTPImpl internal constructor(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: ConsumerSessionStorage,
    private val api: StytchApi.OTP,
) : OTP {
    override val sms: OTP.SmsOTP = SmsOTPImpl()
    override val whatsapp: OTP.WhatsAppOTP = WhatsAppOTPImpl()
    override val email: OTP.EmailOTP = EmailOTPImpl()

    override suspend fun authenticate(parameters: OTP.AuthParameters): AuthResponse {
        val result: AuthResponse
        withContext(dispatchers.io) {
            // call backend endpoint
            result =
                api
                    .authenticateWithOTP(
                        token = parameters.token,
                        methodId = parameters.methodId,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
                    ).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
        }
        return result
    }

    override fun authenticate(
        parameters: OTP.AuthParameters,
        callback: (response: AuthResponse) -> Unit,
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = authenticate(parameters)
            callback(result)
        }
    }

    private inner class SmsOTPImpl : OTP.SmsOTP {
        override suspend fun loginOrCreate(parameters: OTP.SmsOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(dispatchers.io) {
                result =
                    api.loginOrCreateByOTPWithSMS(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                        enableAutofill = parameters.enableAutofill,
                    )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: OTP.SmsOTP.Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

        override suspend fun send(parameters: OTP.SmsOTP.Parameters): OTPSendResponse =
            withContext(dispatchers.io) {
                if (sessionStorage.persistedSessionIdentifiersExist) {
                    api.sendOTPWithSMSSecondary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                } else {
                    api.sendOTPWithSMSPrimary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                }
            }

        override fun send(
            parameters: OTP.SmsOTP.Parameters,
            callback: (response: OTPSendResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }

    private inner class WhatsAppOTPImpl : OTP.WhatsAppOTP {
        override suspend fun loginOrCreate(parameters: OTP.WhatsAppOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(dispatchers.io) {
                result =
                    api.loginOrCreateUserByOTPWithWhatsApp(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: OTP.WhatsAppOTP.Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

        override suspend fun send(parameters: OTP.WhatsAppOTP.Parameters): OTPSendResponse =
            withContext(dispatchers.io) {
                if (sessionStorage.persistedSessionIdentifiersExist) {
                    api.sendOTPWithWhatsAppSecondary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                } else {
                    api.sendOTPWithWhatsAppPrimary(
                        phoneNumber = parameters.phoneNumber,
                        expirationMinutes = parameters.expirationMinutes,
                    )
                }
            }

        override fun send(
            parameters: OTP.WhatsAppOTP.Parameters,
            callback: (response: OTPSendResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }

    private inner class EmailOTPImpl : OTP.EmailOTP {
        override suspend fun loginOrCreate(parameters: OTP.EmailOTP.Parameters): LoginOrCreateOTPResponse {
            val result: LoginOrCreateOTPResponse
            withContext(dispatchers.io) {
                result =
                    api.loginOrCreateUserByOTPWithEmail(
                        email = parameters.email,
                        expirationMinutes = parameters.expirationMinutes,
                        loginTemplateId = parameters.loginTemplateId,
                        signupTemplateId = parameters.signupTemplateId,
                    )
            }

            return result
        }

        override fun loginOrCreate(
            parameters: OTP.EmailOTP.Parameters,
            callback: (response: LoginOrCreateOTPResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = loginOrCreate(parameters)
                callback(result)
            }
        }

        override suspend fun send(parameters: OTP.EmailOTP.Parameters): OTPSendResponse =
            withContext(dispatchers.io) {
                if (sessionStorage.persistedSessionIdentifiersExist) {
                    api.sendOTPWithEmailSecondary(
                        email = parameters.email,
                        expirationMinutes = parameters.expirationMinutes,
                        loginTemplateId = parameters.loginTemplateId,
                        signupTemplateId = parameters.signupTemplateId,
                    )
                } else {
                    api.sendOTPWithEmailPrimary(
                        email = parameters.email,
                        expirationMinutes = parameters.expirationMinutes,
                        loginTemplateId = parameters.loginTemplateId,
                        signupTemplateId = parameters.signupTemplateId,
                    )
                }
            }

        override fun send(
            parameters: OTP.EmailOTP.Parameters,
            callback: (response: OTPSendResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                val result = send(parameters)
                callback(result)
            }
        }
    }
}

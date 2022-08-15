package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OTPImpl internal constructor() : OTP {

    override suspend fun authenticate(authParams: OTP.AuthParameters): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.OTP.authenticateWithOTP(
                token = authParams.token,
                sessionDurationInMinutes = authParams.sessionDurationInMinutes
            )
        }
        return result
    }

    override fun authenticate(authParams: OTP.AuthParameters, callback: (response: BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = authenticate(authParams)
            callback(result)
        }
    }

    override suspend fun loginOrCreateUserWithSMS(params: OTP.PhoneParameters): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.OTP.loginOrCreateByOTPWithSMS(
                phoneNumber = params.phoneNumber,
                expirationInMinutes = params.expirationInMinutes
            )
        }

        return result
    }

    override fun loginOrCreateUserWithSMS(parameters: OTP.PhoneParameters, callback: (response: BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = loginOrCreateUserWithSMS(parameters)
            callback(result)
        }
    }

    override suspend fun loginOrCreateUserWithWhatsapp(parameters: OTP.PhoneParameters): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.OTP.loginOrCreateUserByOTPWithWhatsapp(
                phoneNumber = parameters.phoneNumber,
                expirationInMinutes = parameters.expirationInMinutes
            )
        }

        return result
    }

    override fun loginOrCreateUserWithWhatsapp(parameters: OTP.PhoneParameters, callback: (response: BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = loginOrCreateUserWithWhatsapp(parameters)
            callback(result)
        }
    }

    override suspend fun loginOrCreateUserWithEmail(parameters: OTP.EmailParameters): BaseResponse {
        val result: BaseResponse
        withContext(StytchClient.ioDispatcher) {
            result = StytchApi.OTP.loginOrCreateUserByOTPWithEmail(
                email = parameters.email,
                expirationInMinutes = parameters.expirationInMinutes
            )
        }

        return result
    }

    override fun loginOrCreateUserWithEmail(parameters: OTP.EmailParameters, callback: (response: BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = loginOrCreateUserWithEmail(parameters)
            callback(result)
        }
    }
}

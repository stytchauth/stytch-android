package com.stytch.sdk.b2b.otp

import com.stytch.sdk.b2b.BasicResponse
import com.stytch.sdk.b2b.SMSAuthenticateResponse
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class OTPImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.OTP,
) : OTP {
    override val sms: OTP.SMS = SMSImpl()

    private inner class SMSImpl : OTP.SMS {
        override suspend fun send(parameters: OTP.SMS.SendParameters): BasicResponse =
            withContext(dispatchers.io) {
                api.sendSMSOTP(
                    organizationId = parameters.organizationId,
                    memberId = parameters.memberId,
                    mfaPhoneNumber = parameters.mfaPhoneNumber,
                    locale = parameters.locale,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                )
            }

        override fun send(
            parameters: OTP.SMS.SendParameters,
            callback: (BasicResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(send(parameters))
            }
        }

        override suspend fun authenticate(parameters: OTP.SMS.AuthenticateParameters): SMSAuthenticateResponse =
            withContext(dispatchers.io) {
                api.authenticateSMSOTP(
                    organizationId = parameters.organizationId,
                    memberId = parameters.memberId,
                    code = parameters.code,
                    setMFAEnrollment = parameters.setMFAEnrollment,
                    sessionDurationMinutes = parameters.sessionDurationMinutes.toInt(),
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                ).apply {
                    launchSessionUpdater(dispatchers, sessionStorage)
                }
            }

        override fun authenticate(
            parameters: OTP.SMS.AuthenticateParameters,
            callback: (SMSAuthenticateResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(authenticate(parameters))
            }
        }
    }
}

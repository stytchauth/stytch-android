package com.stytch.sdk.b2b.otp

import com.stytch.sdk.b2b.BasicResponse
import com.stytch.sdk.b2b.EmailOTPAuthenticateResponse
import com.stytch.sdk.b2b.EmailOTPDiscoveryAuthenticateResponse
import com.stytch.sdk.b2b.EmailOTPDiscoverySendResponse
import com.stytch.sdk.b2b.EmailOTPLoginOrSignupResponse
import com.stytch.sdk.b2b.SMSAuthenticateResponse
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.common.StytchDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture

internal class OTPImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: B2BSessionStorage,
    private val api: StytchB2BApi.OTP,
) : OTP {
    override val sms: OTP.SMS = SMSImpl()
    override val email: OTP.Email = EmailImpl()

    private inner class SMSImpl : OTP.SMS {
        override suspend fun send(parameters: OTP.SMS.SendParameters): BasicResponse {
            if (parameters.enableAutofill) {
                StytchB2BClient.startSmsRetriever(parameters.autofillSessionDurationMinutes)
            }
            return withContext(dispatchers.io) {
                api.sendSMSOTP(
                    organizationId = parameters.organizationId,
                    memberId = parameters.memberId,
                    mfaPhoneNumber = parameters.mfaPhoneNumber,
                    locale = parameters.locale,
                    intermediateSessionToken = sessionStorage.intermediateSessionToken,
                    enableAutofill = parameters.enableAutofill,
                )
            }
        }

        override fun send(
            parameters: OTP.SMS.SendParameters,
            callback: (BasicResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(send(parameters))
            }
        }

        override fun sendCompletable(parameters: OTP.SMS.SendParameters): CompletableFuture<BasicResponse> =
            externalScope
                .async {
                    send(parameters)
                }.asCompletableFuture()

        override suspend fun authenticate(parameters: OTP.SMS.AuthenticateParameters): SMSAuthenticateResponse =
            withContext(dispatchers.io) {
                api
                    .authenticateSMSOTP(
                        organizationId = parameters.organizationId,
                        memberId = parameters.memberId,
                        code = parameters.code,
                        setMFAEnrollment = parameters.setMFAEnrollment,
                        sessionDurationMinutes = parameters.sessionDurationMinutes,
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

        override fun authenticateCompletable(
            parameters: OTP.SMS.AuthenticateParameters,
        ): CompletableFuture<SMSAuthenticateResponse> =
            externalScope
                .async {
                    authenticate(parameters)
                }.asCompletableFuture()
    }

    private inner class EmailImpl : OTP.Email {
        override val discovery: OTP.Email.Discovery = EmailDiscoveryImpl()

        override suspend fun loginOrSignup(
            parameters: OTP.Email.LoginOrSignupParameters,
        ): EmailOTPLoginOrSignupResponse =
            withContext(dispatchers.io) {
                api.otpEmailLoginOrSignup(
                    organizationId = parameters.organizationId,
                    emailAddress = parameters.emailAddress,
                    loginTemplateId = parameters.loginTemplateId,
                    signupTemplateId = parameters.signupTemplateId,
                    locale = parameters.locale,
                )
            }

        override fun loginOrSignup(
            parameters: OTP.Email.LoginOrSignupParameters,
            callback: (EmailOTPLoginOrSignupResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(loginOrSignup(parameters))
            }
        }

        override fun loginOrSignupCompletable(
            parameters: OTP.Email.LoginOrSignupParameters,
        ): CompletableFuture<EmailOTPLoginOrSignupResponse> =
            externalScope.async { loginOrSignup(parameters) }.asCompletableFuture()

        override suspend fun authenticate(parameters: OTP.Email.AuthenticateParameters): EmailOTPAuthenticateResponse =
            withContext(dispatchers.io) {
                api.otpEmailAuthenticate(
                    organizationId = parameters.organizationId,
                    emailAddress = parameters.emailAddress,
                    locale = parameters.locale,
                    code = parameters.code,
                    sessionDurationMinutes = parameters.sessionDurationMinutes,
                )
            }

        override fun authenticate(
            parameters: OTP.Email.AuthenticateParameters,
            callback: (EmailOTPAuthenticateResponse) -> Unit,
        ) {
            externalScope.launch(dispatchers.ui) {
                callback(authenticate(parameters))
            }
        }

        override fun authenticateCompletable(
            parameters: OTP.Email.AuthenticateParameters,
        ): CompletableFuture<EmailOTPAuthenticateResponse> =
            externalScope.async { authenticate(parameters) }.asCompletableFuture()

        private inner class EmailDiscoveryImpl : OTP.Email.Discovery {
            override suspend fun send(parameters: OTP.Email.Discovery.SendParameters): EmailOTPDiscoverySendResponse =
                withContext(dispatchers.io) {
                    api.otpEmailDiscoverySend(
                        emailAddress = parameters.emailAddress,
                        loginTemplateId = parameters.loginTemplateId,
                        locale = parameters.locale,
                    )
                }

            override fun send(
                parameters: OTP.Email.Discovery.SendParameters,
                callback: (EmailOTPDiscoverySendResponse) -> Unit,
            ) {
                externalScope.launch(dispatchers.ui) {
                    callback(send(parameters))
                }
            }

            override fun sendCompletable(
                parameters: OTP.Email.Discovery.SendParameters,
            ): CompletableFuture<EmailOTPDiscoverySendResponse> =
                externalScope.async { send(parameters) }.asCompletableFuture()

            override suspend fun authenticate(
                parameters: OTP.Email.Discovery.AuthenticateParameters,
            ): EmailOTPDiscoveryAuthenticateResponse =
                withContext(dispatchers.io) {
                    api.otpEmailDiscoveryAuthenticate(
                        code = parameters.code,
                        emailAddress = parameters.emailAddress,
                    )
                }

            override fun authenticate(
                parameters: OTP.Email.Discovery.AuthenticateParameters,
                callback: (EmailOTPDiscoveryAuthenticateResponse) -> Unit,
            ) {
                externalScope.launch(dispatchers.ui) {
                    callback(authenticate(parameters))
                }
            }

            override fun authenticateCompletable(
                parameters: OTP.Email.Discovery.AuthenticateParameters,
            ): CompletableFuture<EmailOTPDiscoveryAuthenticateResponse> =
                externalScope.async { authenticate(parameters) }.asCompletableFuture()
        }
    }
}

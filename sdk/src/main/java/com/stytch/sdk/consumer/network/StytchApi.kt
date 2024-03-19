package com.stytch.sdk.consumer.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.Constants.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.NoResponseResponse
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.common.network.StytchDFPInterceptor
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.BiometricsStartResponse
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.common.network.models.NoResponseData
import com.stytch.sdk.common.network.models.OTPSendResponseData
import com.stytch.sdk.common.network.safeApiCall
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.WebAuthnAuthenticateStartResponse
import com.stytch.sdk.consumer.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.WebAuthnRegisterStartResponse
import com.stytch.sdk.consumer.WebAuthnUpdateResponse
import com.stytch.sdk.consumer.network.models.AuthData
import com.stytch.sdk.consumer.network.models.BiometricsAuthData
import com.stytch.sdk.consumer.network.models.ConsumerRequests
import com.stytch.sdk.consumer.network.models.CreateResponse
import com.stytch.sdk.consumer.network.models.DeleteAuthenticationFactorData
import com.stytch.sdk.consumer.network.models.NativeOAuthData
import com.stytch.sdk.consumer.network.models.StrengthCheckResponse
import com.stytch.sdk.consumer.network.models.UpdateUserResponseData
import com.stytch.sdk.consumer.network.models.UserData
import com.stytch.sdk.consumer.network.models.UserSearchResponseData

internal object StytchApi {
    internal lateinit var publicToken: String
    private lateinit var deviceInfo: DeviceInfo

    // save reference for changing auth header
    // make sure api is configured before accessing this variable
    @Suppress("MaxLineLength")
    @VisibleForTesting
    internal val authHeaderInterceptor: StytchAuthHeaderInterceptor by lazy {
        if (!isInitialized) {
            throw StytchSDKNotConfiguredError("StytchClient")
        }
        StytchAuthHeaderInterceptor(
            deviceInfo,
            publicToken,
        ) { StytchClient.sessionStorage.sessionToken }
    }

    internal fun configure(
        publicToken: String,
        deviceInfo: DeviceInfo,
    ) {
        this.publicToken = publicToken
        this.deviceInfo = deviceInfo
    }

    internal fun configureDFP(
        dfpProvider: DFPProvider,
        captchaProvider: CaptchaProvider,
        dfpProtectedAuthEnabled: Boolean,
        dfpProtectedAuthMode: DFPProtectedAuthMode,
    ) {
        dfpProtectedStytchApiService =
            ApiService.createApiService(
                Constants.WEB_URL,
                authHeaderInterceptor,
                StytchDFPInterceptor(dfpProvider, captchaProvider, dfpProtectedAuthEnabled, dfpProtectedAuthMode),
                { StytchClient.sessionStorage.revoke() },
                StytchApiService::class.java,
            )
    }

    internal val isInitialized: Boolean
        get() {
            return ::publicToken.isInitialized && ::deviceInfo.isInitialized
        }

    internal val isTestToken: Boolean
        get() {
            StytchClient.assertInitialized()
            return publicToken.contains("public-token-test")
        }

    private val regularStytchApiService: StytchApiService by lazy {
        ApiService.createApiService(
            Constants.WEB_URL,
            authHeaderInterceptor,
            null,
            { StytchClient.sessionStorage.revoke() },
            StytchApiService::class.java,
        )
    }

    private lateinit var dfpProtectedStytchApiService: StytchApiService

    @VisibleForTesting
    internal val apiService: StytchApiService
        get() {
            StytchClient.assertInitialized()
            return if (::dfpProtectedStytchApiService.isInitialized) {
                dfpProtectedStytchApiService
            } else {
                regularStytchApiService
            }
        }

    internal object MagicLinks {
        object Email {
            /** https://stytch.com/docs/api/log-in-or-create-user-by-email */
            @Suppress("LongParameterList")
            suspend fun loginOrCreate(
                email: String,
                loginMagicLinkUrl: String?,
                codeChallenge: String,
                codeChallengeMethod: String,
                loginTemplateId: String?,
                signupTemplateId: String?,
            ): StytchResult<BasicData> =
                safeConsumerApiCall {
                    apiService.loginOrCreateUserByEmail(
                        ConsumerRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                            email = email,
                            loginMagicLinkUrl = loginMagicLinkUrl,
                            codeChallenge = codeChallenge,
                            codeChallengeMethod = codeChallengeMethod,
                            loginTemplateId = loginTemplateId,
                            signupTemplateId = signupTemplateId,
                        ),
                    )
                }

            suspend fun authenticate(
                token: String,
                sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
                codeVerifier: String,
            ): StytchResult<AuthData> =
                safeConsumerApiCall {
                    apiService.authenticate(
                        ConsumerRequests.MagicLinks.AuthenticateRequest(
                            token,
                            codeVerifier,
                            sessionDurationMinutes.toInt(),
                        ),
                    )
                }

            @Suppress("LongParameterList")
            suspend fun sendPrimary(
                email: String,
                loginMagicLinkUrl: String?,
                signupMagicLinkUrl: String?,
                loginExpirationMinutes: Int?,
                signupExpirationMinutes: Int?,
                loginTemplateId: String?,
                signupTemplateId: String?,
                codeChallenge: String?,
            ): StytchResult<BasicData> =
                safeConsumerApiCall {
                    apiService.sendEmailMagicLinkPrimary(
                        ConsumerRequests.MagicLinks.SendRequest(
                            email = email,
                            loginMagicLinkUrl = loginMagicLinkUrl,
                            signupMagicLinkUrl = signupMagicLinkUrl,
                            loginExpirationMinutes = loginExpirationMinutes,
                            signupExpirationMinutes = signupExpirationMinutes,
                            loginTemplateId = loginTemplateId,
                            signupTemplateId = signupTemplateId,
                            codeChallenge = codeChallenge,
                        ),
                    )
                }

            @Suppress("LongParameterList")
            suspend fun sendSecondary(
                email: String,
                loginMagicLinkUrl: String?,
                signupMagicLinkUrl: String?,
                loginExpirationMinutes: Int?,
                signupExpirationMinutes: Int?,
                loginTemplateId: String?,
                signupTemplateId: String?,
                codeChallenge: String?,
            ): StytchResult<BasicData> =
                safeConsumerApiCall {
                    apiService.sendEmailMagicLinkSecondary(
                        ConsumerRequests.MagicLinks.SendRequest(
                            email = email,
                            loginMagicLinkUrl = loginMagicLinkUrl,
                            signupMagicLinkUrl = signupMagicLinkUrl,
                            loginExpirationMinutes = loginExpirationMinutes,
                            signupExpirationMinutes = signupExpirationMinutes,
                            loginTemplateId = loginTemplateId,
                            signupTemplateId = signupTemplateId,
                            codeChallenge = codeChallenge,
                        ),
                    )
                }
        }
    }

    internal object OTP {
        suspend fun loginOrCreateByOTPWithSMS(
            phoneNumber: String,
            expirationMinutes: UInt,
        ): StytchResult<LoginOrCreateOTPData> =
            safeConsumerApiCall {
                apiService.loginOrCreateUserByOTPWithSMS(
                    ConsumerRequests.OTP.SMS(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes.toInt(),
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSPrimary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithSMSPrimary(
                    ConsumerRequests.OTP.SMS(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes?.toInt(),
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSSecondary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithSMSSecondary(
                    ConsumerRequests.OTP.SMS(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes?.toInt(),
                    ),
                )
            }

        suspend fun loginOrCreateUserByOTPWithWhatsApp(
            phoneNumber: String,
            expirationMinutes: UInt,
        ): StytchResult<LoginOrCreateOTPData> =
            safeConsumerApiCall {
                apiService.loginOrCreateUserByOTPWithWhatsApp(
                    ConsumerRequests.OTP.WhatsApp(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes.toInt(),
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppPrimary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithWhatsAppPrimary(
                    ConsumerRequests.OTP.WhatsApp(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes?.toInt(),
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppSecondary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithWhatsAppSecondary(
                    ConsumerRequests.OTP.WhatsApp(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes?.toInt(),
                    ),
                )
            }

        suspend fun loginOrCreateUserByOTPWithEmail(
            email: String,
            expirationMinutes: UInt,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<LoginOrCreateOTPData> =
            safeConsumerApiCall {
                apiService.loginOrCreateUserByOTPWithEmail(
                    ConsumerRequests.OTP.Email(
                        email = email,
                        expirationMinutes = expirationMinutes.toInt(),
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithEmailPrimary(
            email: String,
            expirationMinutes: UInt?,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithEmailPrimary(
                    ConsumerRequests.OTP.Email(
                        email = email,
                        expirationMinutes = expirationMinutes?.toInt(),
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithEmailSecondary(
            email: String,
            expirationMinutes: UInt?,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithEmailSecondary(
                    ConsumerRequests.OTP.Email(
                        email = email,
                        expirationMinutes = expirationMinutes?.toInt(),
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                    ),
                )
            }

        suspend fun authenticateWithOTP(
            token: String,
            methodId: String,
            sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.authenticateWithOTP(
                    ConsumerRequests.OTP.Authenticate(
                        token,
                        methodId,
                        sessionDurationMinutes.toInt(),
                    ),
                )
            }
    }

    internal object Passwords {
        suspend fun authenticate(
            email: String,
            password: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.authenticateWithPasswords(
                    ConsumerRequests.Passwords.AuthenticateRequest(
                        email,
                        password,
                        sessionDurationMinutes.toInt(),
                    ),
                )
            }

        suspend fun create(
            email: String,
            password: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<CreateResponse> =
            safeConsumerApiCall {
                apiService.passwords(
                    ConsumerRequests.Passwords.CreateRequest(
                        email,
                        password,
                        sessionDurationMinutes.toInt(),
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun resetByEmailStart(
            email: String,
            codeChallenge: String,
            codeChallengeMethod: String,
            loginRedirectUrl: String?,
            loginExpirationMinutes: Int?,
            resetPasswordRedirectUrl: String?,
            resetPasswordExpirationMinutes: Int?,
            resetPasswordTemplateId: String?,
        ): StytchResult<BasicData> =
            safeConsumerApiCall {
                apiService.resetByEmailStart(
                    ConsumerRequests.Passwords.ResetByEmailStartRequest(
                        email,
                        codeChallenge,
                        codeChallengeMethod,
                        loginRedirectUrl,
                        loginExpirationMinutes,
                        resetPasswordRedirectUrl,
                        resetPasswordExpirationMinutes,
                        resetPasswordTemplateId,
                    ),
                )
            }

        suspend fun resetByEmail(
            token: String,
            password: String,
            sessionDurationMinutes: UInt,
            codeVerifier: String,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.resetByEmail(
                    ConsumerRequests.Passwords.ResetByEmailRequest(
                        token,
                        password,
                        sessionDurationMinutes.toInt(),
                        codeVerifier,
                    ),
                )
            }

        suspend fun resetBySession(
            password: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.resetBySession(
                    ConsumerRequests.Passwords.ResetBySessionRequest(
                        password = password,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }

        suspend fun strengthCheck(
            email: String?,
            password: String,
        ): StytchResult<StrengthCheckResponse> =
            safeConsumerApiCall {
                apiService.strengthCheck(
                    ConsumerRequests.Passwords.StrengthCheckRequest(
                        email,
                        password,
                    ),
                )
            }
    }

    internal object Sessions {
        suspend fun authenticate(sessionDurationMinutes: UInt? = null): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.authenticateSessions(
                    CommonRequests.Sessions.AuthenticateRequest(
                        sessionDurationMinutes?.toInt(),
                    ),
                )
            }

        suspend fun revoke(): StytchResult<BasicData> =
            safeConsumerApiCall {
                apiService.revokeSessions()
            }
    }

    internal object Biometrics {
        suspend fun registerStart(publicKey: String): StytchResult<BiometricsStartResponse> =
            safeConsumerApiCall {
                apiService.biometricsRegisterStart(
                    ConsumerRequests.Biometrics.RegisterStartRequest(
                        publicKey = publicKey,
                    ),
                )
            }

        suspend fun register(
            signature: String,
            biometricRegistrationId: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<BiometricsAuthData> =
            safeConsumerApiCall {
                apiService.biometricsRegister(
                    ConsumerRequests.Biometrics.RegisterRequest(
                        signature = signature,
                        biometricRegistrationId = biometricRegistrationId,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }

        suspend fun authenticateStart(publicKey: String): StytchResult<BiometricsStartResponse> =
            safeConsumerApiCall {
                apiService.biometricsAuthenticateStart(
                    ConsumerRequests.Biometrics.AuthenticateStartRequest(
                        publicKey = publicKey,
                    ),
                )
            }

        suspend fun authenticate(
            signature: String,
            biometricRegistrationId: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<BiometricsAuthData> =
            safeConsumerApiCall {
                apiService.biometricsAuthenticate(
                    ConsumerRequests.Biometrics.AuthenticateRequest(
                        signature = signature,
                        biometricRegistrationId = biometricRegistrationId,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }
    }

    internal object UserManagement {
        suspend fun getUser(): StytchResult<UserData> =
            safeConsumerApiCall {
                apiService.getUser()
            }

        suspend fun deleteEmailById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeConsumerApiCall {
                apiService.deleteEmailById(id)
            }

        suspend fun deletePhoneNumberById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeConsumerApiCall {
                apiService.deletePhoneNumberById(id)
            }

        suspend fun deleteBiometricRegistrationById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeConsumerApiCall {
                apiService.deleteBiometricRegistrationById(id)
            }

        suspend fun deleteCryptoWalletById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeConsumerApiCall {
                apiService.deleteCryptoWalletById(id)
            }

        suspend fun deleteWebAuthnById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeConsumerApiCall {
                apiService.deleteWebAuthnById(id)
            }

        suspend fun updateUser(
            name: NameData?,
            untrustedMetadata: Map<String, Any?>?,
        ): StytchResult<UpdateUserResponseData> =
            safeConsumerApiCall {
                apiService.updateUser(
                    ConsumerRequests.User.UpdateRequest(
                        name = name,
                        untrustedMetadata = untrustedMetadata,
                    ),
                )
            }

        suspend fun searchUsers(email: String): StytchResult<UserSearchResponseData> =
            safeConsumerApiCall {
                apiService.searchUsers(
                    ConsumerRequests.User.SearchRequest(
                        email = email,
                    ),
                )
            }
    }

    internal object OAuth {
        suspend fun authenticateWithGoogleIdToken(
            idToken: String,
            nonce: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<NativeOAuthData> =
            safeConsumerApiCall {
                apiService.authenticateWithGoogleIdToken(
                    ConsumerRequests.OAuth.Google.AuthenticateRequest(
                        idToken = idToken,
                        nonce = nonce,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }

        suspend fun authenticateWithThirdPartyToken(
            token: String,
            sessionDurationMinutes: UInt,
            codeVerifier: String,
        ): OAuthAuthenticatedResponse =
            safeConsumerApiCall {
                apiService.authenticateWithThirdPartyToken(
                    ConsumerRequests.OAuth.ThirdParty.AuthenticateRequest(
                        token = token,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                        codeVerifier = codeVerifier,
                    ),
                )
            }
    }

    internal object WebAuthn {
        suspend fun registerStart(
            domain: String,
            userAgent: String? = null,
            authenticatorType: String? = null,
            isPasskey: Boolean = false,
        ): WebAuthnRegisterStartResponse =
            safeConsumerApiCall {
                apiService.webAuthnRegisterStart(
                    ConsumerRequests.WebAuthn.RegisterStartRequest(
                        domain = domain,
                        userAgent = userAgent,
                        authenticatorType = authenticatorType,
                        isPasskey = isPasskey,
                    ),
                )
            }

        suspend fun register(publicKeyCredential: String): WebAuthnRegisterResponse =
            safeConsumerApiCall {
                apiService.webAuthnRegister(
                    ConsumerRequests.WebAuthn.RegisterRequest(
                        publicKeyCredential = publicKeyCredential,
                    ),
                )
            }

        suspend fun authenticateStartPrimary(
            domain: String,
            isPasskey: Boolean = false,
        ): WebAuthnAuthenticateStartResponse =
            safeConsumerApiCall {
                apiService.webAuthnAuthenticateStartPrimary(
                    ConsumerRequests.WebAuthn.AuthenticateStartRequest(
                        domain = domain,
                        isPasskey = isPasskey,
                    ),
                )
            }

        suspend fun authenticateStartSecondary(
            domain: String,
            isPasskey: Boolean = false,
        ): WebAuthnAuthenticateStartResponse =
            safeConsumerApiCall {
                apiService.webAuthnAuthenticateStartSecondary(
                    ConsumerRequests.WebAuthn.AuthenticateStartRequest(
                        domain = domain,
                        isPasskey = isPasskey,
                    ),
                )
            }

        suspend fun authenticate(
            publicKeyCredential: String,
            sessionDurationMinutes: UInt,
        ): AuthResponse =
            safeConsumerApiCall {
                apiService.webAuthnAuthenticate(
                    ConsumerRequests.WebAuthn.AuthenticateRequest(
                        publicKeyCredential = publicKeyCredential,
                        sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    ),
                )
            }

        suspend fun update(
            id: String,
            name: String,
        ): WebAuthnUpdateResponse =
            safeConsumerApiCall {
                apiService.webAuthnUpdate(
                    id = id,
                    ConsumerRequests.WebAuthn.UpdateRequest(
                        name = name,
                    ),
                )
            }
    }

    suspend fun getBootstrapData(): StytchResult<BootstrapData> =
        safeConsumerApiCall {
            apiService.getBootstrapData(publicToken = publicToken)
        }

    internal object Events {
        suspend fun logEvent(
            eventId: String,
            appSessionId: String,
            persistentId: String,
            clientSentAt: String,
            timezone: String,
            eventName: String,
            infoHeaderModel: InfoHeaderModel,
            details: Map<String, Any>? = null,
            error: Exception? = null,
        ): NoResponseResponse =
            safeConsumerApiCall {
                apiService.logEvent(
                    listOf(
                        CommonRequests.Events.Event(
                            telemetry =
                                CommonRequests.Events.EventTelemetry(
                                    eventId = eventId,
                                    appSessionId = appSessionId,
                                    persistentId = persistentId,
                                    clientSentAt = clientSentAt,
                                    timezone = timezone,
                                    app =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = infoHeaderModel.app.identifier,
                                            version = infoHeaderModel.app.version,
                                        ),
                                    sdk =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = infoHeaderModel.sdk.identifier,
                                            version = infoHeaderModel.sdk.version,
                                        ),
                                    os =
                                        CommonRequests.Events.VersionIdentifier(
                                            identifier = infoHeaderModel.os.identifier,
                                            version = infoHeaderModel.os.version,
                                        ),
                                    device =
                                        CommonRequests.Events.DeviceIdentifier(
                                            model = infoHeaderModel.device.identifier,
                                            screenSize = infoHeaderModel.device.version,
                                        ),
                                ),
                            event =
                                CommonRequests.Events.EventEvent(
                                    publicToken = publicToken,
                                    eventName = eventName,
                                    details = details,
                                    errorDescription = error?.message,
                                ),
                        ),
                    ),
                )
                // Endpoint returns null, but we expect _something_
                StytchDataResponse(NoResponseData())
            }
    }

    internal suspend fun <T1, T : StytchDataResponse<T1>> safeConsumerApiCall(
        apiCall: suspend () -> T,
    ): StytchResult<T1> =
        safeApiCall({ StytchClient.assertInitialized() }) {
            apiCall()
        }
}

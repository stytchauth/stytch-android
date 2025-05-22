package com.stytch.sdk.consumer.network

import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EndpointOptions
import com.stytch.sdk.common.NoResponseResponse
import com.stytch.sdk.common.SDK_URL_PATH
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.DFPConfiguration
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.events.EventsAPI
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.CommonApi
import com.stytch.sdk.common.network.InfoHeaderModel
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.common.network.StytchDFPInterceptor
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.ThreadingInterceptor
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.BiometricsStartResponse
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.common.network.models.NoResponseData
import com.stytch.sdk.common.network.models.OTPSendResponseData
import com.stytch.sdk.common.network.safeApiCall
import com.stytch.sdk.consumer.AuthResponse
import com.stytch.sdk.consumer.CryptoWalletAuthenticateStartResponse
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.WebAuthnAuthenticateStartResponse
import com.stytch.sdk.consumer.WebAuthnRegisterResponse
import com.stytch.sdk.consumer.WebAuthnRegisterStartResponse
import com.stytch.sdk.consumer.WebAuthnUpdateResponse
import com.stytch.sdk.consumer.network.models.AuthData
import com.stytch.sdk.consumer.network.models.BiometricsAuthData
import com.stytch.sdk.consumer.network.models.ConsumerRequests
import com.stytch.sdk.consumer.network.models.CreateResponse
import com.stytch.sdk.consumer.network.models.CryptoWalletType
import com.stytch.sdk.consumer.network.models.DeleteAuthenticationFactorData
import com.stytch.sdk.consumer.network.models.NativeOAuthData
import com.stytch.sdk.consumer.network.models.StrengthCheckResponse
import com.stytch.sdk.consumer.network.models.TOTPAuthenticateResponseData
import com.stytch.sdk.consumer.network.models.TOTPCreateResponseData
import com.stytch.sdk.consumer.network.models.TOTPRecoverResponseData
import com.stytch.sdk.consumer.network.models.TOTPRecoveryCodesResponseData
import com.stytch.sdk.consumer.network.models.UpdateUserResponseData
import com.stytch.sdk.consumer.network.models.UserData
import com.stytch.sdk.consumer.network.models.UserSearchResponseData

internal object StytchApi : CommonApi {
    internal lateinit var publicToken: String
    private lateinit var deviceInfo: DeviceInfo
    private lateinit var apiServiceClass: ApiService
    private lateinit var dfpInterceptor: StytchDFPInterceptor
    private lateinit var endpointOptions: EndpointOptions

    override fun configure(
        publicToken: String,
        deviceInfo: DeviceInfo,
        endpointOptions: EndpointOptions,
        getSessionToken: () -> String?,
        dfpConfiguration: DFPConfiguration,
    ) {
        this.publicToken = publicToken
        this.deviceInfo = deviceInfo
        this.dfpInterceptor = StytchDFPInterceptor(dfpConfiguration)
        this.endpointOptions = endpointOptions
        apiServiceClass =
            ApiService(
                sdkUrl,
                listOf(
                    ThreadingInterceptor(),
                    StytchAuthHeaderInterceptor(deviceInfo, publicToken, getSessionToken),
                    dfpInterceptor,
                ),
            )
        apiServiceClass
        apiService = apiServiceClass.retrofit.create(StytchApiService::class.java)
    }

    override fun configureDFP(dfpConfiguration: DFPConfiguration) {
        dfpInterceptor.dfpConfiguration = dfpConfiguration
    }

    internal val isInitialized: Boolean
        get() {
            return ::publicToken.isInitialized && ::deviceInfo.isInitialized
        }

    internal val isTestToken: Boolean
        get() {
            assertInitialized()
            return publicToken.contains("public-token-test")
        }

    private val sdkUrl: String by lazy {
        "https://${if (isTestToken) endpointOptions.testDomain else endpointOptions.liveDomain}$SDK_URL_PATH"
    }

    internal fun assertInitialized() {
        if (!isInitialized) {
            throw StytchSDKNotConfiguredError("StytchClient")
        }
    }

    internal lateinit var apiService: StytchApiService

    internal object MagicLinks {
        object Email {
            /** https://stytch.com/docs/api/log-in-or-create-user-by-email */
            @Suppress("LongParameterList")
            suspend fun loginOrCreate(
                email: String,
                loginMagicLinkUrl: String?,
                signupMagicLinkUrl: String?,
                codeChallenge: String,
                loginTemplateId: String?,
                signupTemplateId: String?,
                locale: Locale? = null,
            ): StytchResult<BasicData> =
                safeConsumerApiCall {
                    apiService.loginOrCreateUserByEmail(
                        ConsumerRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                            email = email,
                            loginMagicLinkUrl = loginMagicLinkUrl,
                            signupMagicLinkUrl = signupMagicLinkUrl,
                            codeChallenge = codeChallenge,
                            loginTemplateId = loginTemplateId,
                            signupTemplateId = signupTemplateId,
                            locale = locale,
                        ),
                    )
                }

            suspend fun authenticate(
                token: String,
                sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
                codeVerifier: String,
            ): StytchResult<AuthData> =
                safeConsumerApiCall {
                    apiService.authenticate(
                        ConsumerRequests.MagicLinks.AuthenticateRequest(
                            token = token,
                            codeVerifier = codeVerifier,
                            sessionDurationMinutes = sessionDurationMinutes,
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
                locale: Locale? = null,
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
                            locale = locale,
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
                locale: Locale? = null,
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
                            locale = locale,
                        ),
                    )
                }
        }
    }

    internal object OTP {
        suspend fun loginOrCreateByOTPWithSMS(
            phoneNumber: String,
            expirationMinutes: Int,
            enableAutofill: Boolean = false,
            locale: Locale? = null,
        ): StytchResult<LoginOrCreateOTPData> =
            safeConsumerApiCall {
                apiService.loginOrCreateUserByOTPWithSMS(
                    ConsumerRequests.OTP.SMS(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes,
                        enableAutofill = enableAutofill,
                        locale = locale,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSPrimary(
            phoneNumber: String,
            expirationMinutes: Int?,
            locale: Locale? = null,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithSMSPrimary(
                    ConsumerRequests.OTP.SMS(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes,
                        locale = locale,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSSecondary(
            phoneNumber: String,
            expirationMinutes: Int?,
            locale: Locale? = null,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithSMSSecondary(
                    ConsumerRequests.OTP.SMS(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes,
                        locale = locale,
                    ),
                )
            }

        suspend fun loginOrCreateUserByOTPWithWhatsApp(
            phoneNumber: String,
            expirationMinutes: Int,
        ): StytchResult<LoginOrCreateOTPData> =
            safeConsumerApiCall {
                apiService.loginOrCreateUserByOTPWithWhatsApp(
                    ConsumerRequests.OTP.WhatsApp(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppPrimary(
            phoneNumber: String,
            expirationMinutes: Int?,
            locale: Locale?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithWhatsAppPrimary(
                    ConsumerRequests.OTP.WhatsApp(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes,
                        locale = locale,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppSecondary(
            phoneNumber: String,
            expirationMinutes: Int?,
            locale: Locale?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithWhatsAppSecondary(
                    ConsumerRequests.OTP.WhatsApp(
                        phoneNumber = phoneNumber,
                        expirationMinutes = expirationMinutes,
                        locale = locale,
                    ),
                )
            }

        suspend fun loginOrCreateUserByOTPWithEmail(
            email: String,
            expirationMinutes: Int,
            loginTemplateId: String?,
            signupTemplateId: String?,
            locale: Locale?,
        ): StytchResult<LoginOrCreateOTPData> =
            safeConsumerApiCall {
                apiService.loginOrCreateUserByOTPWithEmail(
                    ConsumerRequests.OTP.Email(
                        email = email,
                        expirationMinutes = expirationMinutes,
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                        locale = locale,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithEmailPrimary(
            email: String,
            expirationMinutes: Int?,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithEmailPrimary(
                    ConsumerRequests.OTP.Email(
                        email = email,
                        expirationMinutes = expirationMinutes,
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithEmailSecondary(
            email: String,
            expirationMinutes: Int?,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<OTPSendResponseData> =
            safeConsumerApiCall {
                apiService.sendOTPWithEmailSecondary(
                    ConsumerRequests.OTP.Email(
                        email = email,
                        expirationMinutes = expirationMinutes,
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                    ),
                )
            }

        suspend fun authenticateWithOTP(
            token: String,
            methodId: String,
            sessionDurationMinutes: Int = DEFAULT_SESSION_TIME_MINUTES,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.authenticateWithOTP(
                    ConsumerRequests.OTP.Authenticate(
                        token,
                        methodId,
                        sessionDurationMinutes,
                    ),
                )
            }
    }

    internal object Passwords {
        suspend fun authenticate(
            email: String,
            password: String,
            sessionDurationMinutes: Int,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.authenticateWithPasswords(
                    ConsumerRequests.Passwords.AuthenticateRequest(
                        email,
                        password,
                        sessionDurationMinutes,
                    ),
                )
            }

        suspend fun create(
            email: String,
            password: String,
            sessionDurationMinutes: Int,
        ): StytchResult<CreateResponse> =
            safeConsumerApiCall {
                apiService.passwords(
                    ConsumerRequests.Passwords.CreateRequest(
                        email,
                        password,
                        sessionDurationMinutes,
                    ),
                )
            }

        @Suppress("LongParameterList")
        suspend fun resetByEmailStart(
            email: String,
            codeChallenge: String,
            loginRedirectUrl: String?,
            loginExpirationMinutes: Int?,
            resetPasswordRedirectUrl: String?,
            resetPasswordExpirationMinutes: Int?,
            resetPasswordTemplateId: String?,
            locale: Locale?,
        ): StytchResult<BasicData> =
            safeConsumerApiCall {
                apiService.resetByEmailStart(
                    ConsumerRequests.Passwords.ResetByEmailStartRequest(
                        email,
                        codeChallenge,
                        loginRedirectUrl,
                        loginExpirationMinutes,
                        resetPasswordRedirectUrl,
                        resetPasswordExpirationMinutes,
                        resetPasswordTemplateId,
                        locale,
                    ),
                )
            }

        suspend fun resetByEmail(
            token: String,
            password: String,
            sessionDurationMinutes: Int,
            codeVerifier: String,
            locale: Locale?,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.resetByEmail(
                    ConsumerRequests.Passwords.ResetByEmailRequest(
                        token,
                        password,
                        sessionDurationMinutes,
                        codeVerifier,
                        locale,
                    ),
                )
            }

        suspend fun resetBySession(
            password: String,
            sessionDurationMinutes: Int,
            locale: Locale?,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.resetBySession(
                    ConsumerRequests.Passwords.ResetBySessionRequest(
                        password = password,
                        sessionDurationMinutes = sessionDurationMinutes,
                        locale = locale,
                    ),
                )
            }

        suspend fun resetByExisting(
            email: String,
            existingPassword: String,
            newPassword: String,
            sessionDurationMinutes: Int,
        ): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.resetByExistingPassword(
                    ConsumerRequests.Passwords.PasswordResetByExistingPasswordRequest(
                        email = email,
                        existingPassword = existingPassword,
                        newPassword = newPassword,
                        sessionDurationMinutes = sessionDurationMinutes,
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
        suspend fun authenticate(sessionDurationMinutes: Int? = null): StytchResult<AuthData> =
            safeConsumerApiCall {
                apiService.authenticateSessions(
                    CommonRequests.Sessions.AuthenticateRequest(
                        sessionDurationMinutes,
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
            sessionDurationMinutes: Int,
        ): StytchResult<BiometricsAuthData> =
            safeConsumerApiCall {
                apiService.biometricsRegister(
                    ConsumerRequests.Biometrics.RegisterRequest(
                        signature = signature,
                        biometricRegistrationId = biometricRegistrationId,
                        sessionDurationMinutes = sessionDurationMinutes,
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
            sessionDurationMinutes: Int,
        ): StytchResult<BiometricsAuthData> =
            safeConsumerApiCall {
                apiService.biometricsAuthenticate(
                    ConsumerRequests.Biometrics.AuthenticateRequest(
                        signature = signature,
                        biometricRegistrationId = biometricRegistrationId,
                        sessionDurationMinutes = sessionDurationMinutes,
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

        suspend fun deleteTotpById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeConsumerApiCall {
                apiService.deleteTOTPById(id)
            }

        suspend fun deleteOAuthById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeConsumerApiCall {
                apiService.deleteOAuthById(id)
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
            sessionDurationMinutes: Int,
        ): StytchResult<NativeOAuthData> =
            safeConsumerApiCall {
                apiService.authenticateWithGoogleIdToken(
                    ConsumerRequests.OAuth.Google.AuthenticateRequest(
                        idToken = idToken,
                        nonce = nonce,
                        sessionDurationMinutes = sessionDurationMinutes,
                    ),
                )
            }

        suspend fun authenticateWithThirdPartyToken(
            token: String,
            sessionDurationMinutes: Int,
            codeVerifier: String,
        ): OAuthAuthenticatedResponse =
            safeConsumerApiCall {
                apiService.authenticateWithThirdPartyToken(
                    ConsumerRequests.OAuth.ThirdParty.AuthenticateRequest(
                        token = token,
                        sessionDurationMinutes = sessionDurationMinutes,
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
            sessionDurationMinutes: Int,
        ): AuthResponse =
            safeConsumerApiCall {
                apiService.webAuthnAuthenticate(
                    ConsumerRequests.WebAuthn.AuthenticateRequest(
                        publicKeyCredential = publicKeyCredential,
                        sessionDurationMinutes = sessionDurationMinutes,
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

    internal object Crypto {
        suspend fun authenticateStartPrimary(
            cryptoWalletAddress: String,
            cryptoWalletType: CryptoWalletType,
        ): CryptoWalletAuthenticateStartResponse =
            safeConsumerApiCall {
                apiService.cryptoWalletAuthenticateStartPrimary(
                    ConsumerRequests.Crypto.CryptoWalletAuthenticateStartRequest(
                        cryptoWalletAddress = cryptoWalletAddress,
                        cryptoWalletType = cryptoWalletType,
                    ),
                )
            }

        suspend fun authenticateStartSecondary(
            cryptoWalletAddress: String,
            cryptoWalletType: CryptoWalletType,
        ): CryptoWalletAuthenticateStartResponse =
            safeConsumerApiCall {
                apiService.cryptoWalletAuthenticateStartSecondary(
                    ConsumerRequests.Crypto.CryptoWalletAuthenticateStartRequest(
                        cryptoWalletAddress = cryptoWalletAddress,
                        cryptoWalletType = cryptoWalletType,
                    ),
                )
            }

        suspend fun authenticate(
            cryptoWalletAddress: String,
            cryptoWalletType: CryptoWalletType,
            signature: String,
            sessionDurationMinutes: Int,
        ): AuthResponse =
            safeConsumerApiCall {
                apiService.cryptoWalletAuthenticate(
                    ConsumerRequests.Crypto.CryptoWalletAuthenticateRequest(
                        cryptoWalletAddress = cryptoWalletAddress,
                        cryptoWalletType = cryptoWalletType,
                        signature = signature,
                        sessionDurationMinutes = sessionDurationMinutes,
                    ),
                )
            }
    }

    internal object TOTP {
        suspend fun create(expirationMinutes: Int): StytchResult<TOTPCreateResponseData> =
            safeConsumerApiCall {
                apiService.totpsCreate(
                    ConsumerRequests.TOTP.TOTPCreateRequest(
                        expirationMinutes = expirationMinutes,
                    ),
                )
            }

        suspend fun authenticate(
            totpCode: String,
            sessionDurationMinutes: Int,
        ): StytchResult<TOTPAuthenticateResponseData> =
            safeConsumerApiCall {
                apiService.totpsAuthenticate(
                    ConsumerRequests.TOTP.TOTPAuthenticateRequest(
                        totpCode = totpCode,
                        sessionDurationMinutes = sessionDurationMinutes,
                    ),
                )
            }

        suspend fun recoveryCodes(): StytchResult<TOTPRecoveryCodesResponseData> =
            safeConsumerApiCall {
                apiService.totpsRecoveryCodes()
            }

        suspend fun recover(
            recoveryCode: String,
            sessionDurationMinutes: Int,
        ): StytchResult<TOTPRecoverResponseData> =
            safeConsumerApiCall {
                apiService.totpsRecover(
                    ConsumerRequests.TOTP.TOTPRecoverRequest(
                        recoveryCode = recoveryCode,
                        sessionDurationMinutes = sessionDurationMinutes,
                    ),
                )
            }
    }

    override suspend fun getBootstrapData(): StytchResult<BootstrapData> =
        safeConsumerApiCall {
            apiService.getBootstrapData(publicToken = publicToken)
        }

    internal object Events : EventsAPI {
        override suspend fun logEvent(
            eventId: String,
            appSessionId: String,
            persistentId: String,
            clientSentAt: String,
            timezone: String,
            eventName: String,
            infoHeaderModel: InfoHeaderModel,
            details: Map<String, Any>?,
            error: Exception?,
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
        safeApiCall({ assertInitialized() }) {
            apiCall()
        }
}

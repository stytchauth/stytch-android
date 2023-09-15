package com.stytch.sdk.consumer.network

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.Constants.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.CaptchaProvider
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.network.ApiService
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.common.network.StytchDFPInterceptor
import com.stytch.sdk.common.network.StytchDataResponse
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.common.network.models.BiometricsStartResponse
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.CommonRequests
import com.stytch.sdk.common.network.models.DFPProtectedAuthEnabled
import com.stytch.sdk.common.network.models.LoginOrCreateOTPData
import com.stytch.sdk.common.network.models.NameData
import com.stytch.sdk.common.network.safeApiCall
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.network.models.AuthData
import com.stytch.sdk.consumer.network.models.BiometricsAuthData
import com.stytch.sdk.consumer.network.models.ConsumerRequests
import com.stytch.sdk.consumer.network.models.CreateResponse
import com.stytch.sdk.consumer.network.models.DeleteAuthenticationFactorData
import com.stytch.sdk.consumer.network.models.NativeOAuthData
import com.stytch.sdk.consumer.network.models.StrengthCheckResponse
import com.stytch.sdk.consumer.network.models.UpdateUserResponseData
import com.stytch.sdk.consumer.network.models.UserData

internal object StytchApi {

    internal lateinit var publicToken: String
    private lateinit var deviceInfo: DeviceInfo

    // save reference for changing auth header
    // make sure api is configured before accessing this variable
    @Suppress("MaxLineLength")
    @VisibleForTesting
    internal val authHeaderInterceptor: StytchAuthHeaderInterceptor by lazy {
        if (!isInitialized) {
            throw StytchExceptions.Critical(
                RuntimeException(
                    "StytchApi not configured. You must call 'StytchApi.configure(...)' before using any functionality of the StytchApi." // ktlint-disable max-line-length
                )
            )
        }
        StytchAuthHeaderInterceptor(
            deviceInfo,
            publicToken,
        ) { StytchClient.sessionStorage.sessionToken }
    }

    internal fun configure(publicToken: String, deviceInfo: DeviceInfo) {
        this.publicToken = publicToken
        this.deviceInfo = deviceInfo
    }

    internal fun configureDFP(
        dfpProvider: DFPProvider,
        captchaProvider: CaptchaProvider,
        dfpProtectedAuthEnabled: DFPProtectedAuthEnabled
    ) {
        dfpProtectedStytchApiService = ApiService.createApiService(
            Constants.WEB_URL,
            authHeaderInterceptor,
            StytchDFPInterceptor(dfpProvider, captchaProvider, dfpProtectedAuthEnabled),
            { StytchClient.sessionStorage.revoke() },
            StytchApiService::class.java
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
        StytchClient.assertInitialized()
        ApiService.createApiService(
            Constants.WEB_URL,
            authHeaderInterceptor,
            null,
            { StytchClient.sessionStorage.revoke() },
            StytchApiService::class.java
        )
    }

    private lateinit var dfpProtectedStytchApiService: StytchApiService

    @VisibleForTesting
    internal val apiService: StytchApiService
        get() = if (::dfpProtectedStytchApiService.isInitialized) {
            dfpProtectedStytchApiService
        } else {
            regularStytchApiService
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
            ): StytchResult<BasicData> = safeConsumerApiCall {
                apiService.loginOrCreateUserByEmail(
                    ConsumerRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                        email = email,
                        loginMagicLinkUrl = loginMagicLinkUrl,
                        codeChallenge = codeChallenge,
                        codeChallengeMethod = codeChallengeMethod,
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                    )
                )
            }

            suspend fun authenticate(
                token: String,
                sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES,
                codeVerifier: String
            ): StytchResult<AuthData> = safeConsumerApiCall {
                apiService.authenticate(
                    ConsumerRequests.MagicLinks.AuthenticateRequest(
                        token,
                        codeVerifier,
                        sessionDurationMinutes.toInt()
                    )
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
            ): StytchResult<BasicData> = safeConsumerApiCall {
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
                    )
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
            ): StytchResult<BasicData> = safeConsumerApiCall {
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
                    )
                )
            }
        }
    }

    internal object OTP {
        suspend fun loginOrCreateByOTPWithSMS(
            phoneNumber: String,
            expirationMinutes: UInt
        ): StytchResult<LoginOrCreateOTPData> = safeConsumerApiCall {
            apiService.loginOrCreateUserByOTPWithSMS(
                ConsumerRequests.OTP.SMS(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSPrimary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.sendOTPWithSMSPrimary(
                ConsumerRequests.OTP.SMS(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSSecondary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.sendOTPWithSMSSecondary(
                ConsumerRequests.OTP.SMS(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithWhatsApp(
            phoneNumber: String,
            expirationMinutes: UInt
        ): StytchResult<LoginOrCreateOTPData> = safeConsumerApiCall {
            apiService.loginOrCreateUserByOTPWithWhatsApp(
                ConsumerRequests.OTP.WhatsApp(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppPrimary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.sendOTPWithWhatsAppPrimary(
                ConsumerRequests.OTP.WhatsApp(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppSecondary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.sendOTPWithWhatsAppSecondary(
                ConsumerRequests.OTP.WhatsApp(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithEmail(
            email: String,
            expirationMinutes: UInt,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<LoginOrCreateOTPData> = safeConsumerApiCall {
            apiService.loginOrCreateUserByOTPWithEmail(
                ConsumerRequests.OTP.Email(
                    email = email,
                    expirationMinutes = expirationMinutes.toInt(),
                    loginTemplateId = loginTemplateId,
                    signupTemplateId = signupTemplateId,
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithEmailPrimary(
            email: String,
            expirationMinutes: UInt?,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.sendOTPWithEmailPrimary(
                ConsumerRequests.OTP.Email(
                    email = email,
                    expirationMinutes = expirationMinutes?.toInt(),
                    loginTemplateId = loginTemplateId,
                    signupTemplateId = signupTemplateId,
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithEmailSecondary(
            email: String,
            expirationMinutes: UInt?,
            loginTemplateId: String?,
            signupTemplateId: String?,
        ): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.sendOTPWithEmailSecondary(
                ConsumerRequests.OTP.Email(
                    email = email,
                    expirationMinutes = expirationMinutes?.toInt(),
                    loginTemplateId = loginTemplateId,
                    signupTemplateId = signupTemplateId,
                )
            )
        }

        suspend fun authenticateWithOTP(
            token: String,
            methodId: String,
            sessionDurationMinutes: UInt = DEFAULT_SESSION_TIME_MINUTES
        ): StytchResult<AuthData> = safeConsumerApiCall {
            apiService.authenticateWithOTP(
                ConsumerRequests.OTP.Authenticate(
                    token,
                    methodId,
                    sessionDurationMinutes.toInt()
                )
            )
        }
    }

    internal object Passwords {

        suspend fun authenticate(
            email: String,
            password: String,
            sessionDurationMinutes: UInt
        ): StytchResult<AuthData> = safeConsumerApiCall {
            apiService.authenticateWithPasswords(
                ConsumerRequests.Passwords.AuthenticateRequest(
                    email,
                    password,
                    sessionDurationMinutes.toInt()
                )
            )
        }

        suspend fun create(
            email: String,
            password: String,
            sessionDurationMinutes: UInt
        ): StytchResult<CreateResponse> = safeConsumerApiCall {
            apiService.passwords(
                ConsumerRequests.Passwords.CreateRequest(
                    email,
                    password,
                    sessionDurationMinutes.toInt()
                )
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
        ): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.resetByEmailStart(
                ConsumerRequests.Passwords.ResetByEmailStartRequest(
                    email,
                    codeChallenge,
                    codeChallengeMethod,
                    loginRedirectUrl,
                    loginExpirationMinutes,
                    resetPasswordRedirectUrl,
                    resetPasswordExpirationMinutes,
                    resetPasswordTemplateId
                )
            )
        }

        suspend fun resetByEmail(
            token: String,
            password: String,
            sessionDurationMinutes: UInt,
            codeVerifier: String
        ): StytchResult<AuthData> = safeConsumerApiCall {
            apiService.resetByEmail(
                ConsumerRequests.Passwords.ResetByEmailRequest(
                    token,
                    password,
                    sessionDurationMinutes.toInt(),
                    codeVerifier
                )
            )
        }

        suspend fun strengthCheck(
            email: String?,
            password: String
        ): StytchResult<StrengthCheckResponse> = safeConsumerApiCall {
            apiService.strengthCheck(
                ConsumerRequests.Passwords.StrengthCheckRequest(
                    email,
                    password
                )
            )
        }
    }

    internal object Sessions {

        suspend fun authenticate(
            sessionDurationMinutes: UInt? = null
        ): StytchResult<AuthData> = safeConsumerApiCall {
            apiService.authenticateSessions(
                CommonRequests.Sessions.AuthenticateRequest(
                    sessionDurationMinutes?.toInt()
                )
            )
        }

        suspend fun revoke(): StytchResult<BasicData> = safeConsumerApiCall {
            apiService.revokeSessions()
        }
    }

    internal object Biometrics {
        suspend fun registerStart(
            publicKey: String,
        ): StytchResult<BiometricsStartResponse> = safeConsumerApiCall {
            apiService.biometricsRegisterStart(
                ConsumerRequests.Biometrics.RegisterStartRequest(
                    publicKey = publicKey,
                )
            )
        }

        suspend fun register(
            signature: String,
            biometricRegistrationId: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<BiometricsAuthData> = safeConsumerApiCall {
            apiService.biometricsRegister(
                ConsumerRequests.Biometrics.RegisterRequest(
                    signature = signature,
                    biometricRegistrationId = biometricRegistrationId,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                )
            )
        }

        suspend fun authenticateStart(
            publicKey: String,
        ): StytchResult<BiometricsStartResponse> = safeConsumerApiCall {
            apiService.biometricsAuthenticateStart(
                ConsumerRequests.Biometrics.AuthenticateStartRequest(
                    publicKey = publicKey,
                )
            )
        }

        suspend fun authenticate(
            signature: String,
            biometricRegistrationId: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<BiometricsAuthData> = safeConsumerApiCall {
            apiService.biometricsAuthenticate(
                ConsumerRequests.Biometrics.AuthenticateRequest(
                    signature = signature,
                    biometricRegistrationId = biometricRegistrationId,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                )
            )
        }
    }

    internal object UserManagement {
        suspend fun getUser(): StytchResult<UserData> = safeConsumerApiCall {
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
                        untrustedMetadata = untrustedMetadata
                    )
                )
            }
    }

    internal object OAuth {
        suspend fun authenticateWithGoogleIdToken(
            idToken: String,
            nonce: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<NativeOAuthData> = safeConsumerApiCall {
            apiService.authenticateWithGoogleIdToken(
                ConsumerRequests.OAuth.Google.AuthenticateRequest(
                    idToken = idToken,
                    nonce = nonce,
                    sessionDurationMinutes = sessionDurationMinutes.toInt()
                )
            )
        }

        suspend fun authenticateWithThirdPartyToken(
            token: String,
            sessionDurationMinutes: UInt,
            codeVerifier: String,
        ): OAuthAuthenticatedResponse = safeConsumerApiCall {
            apiService.authenticateWithThirdPartyToken(
                ConsumerRequests.OAuth.ThirdParty.AuthenticateRequest(
                    token = token,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    codeVerifier = codeVerifier
                )
            )
        }
    }

    suspend fun getBootstrapData(): StytchResult<BootstrapData> = safeConsumerApiCall {
        apiService.getBootstrapData(publicToken = publicToken)
    }

    internal suspend fun <T1, T : StytchDataResponse<T1>> safeConsumerApiCall(
        apiCall: suspend () -> T
    ): StytchResult<T1> = safeApiCall({ StytchClient.assertInitialized() }) {
        apiCall()
    }
}

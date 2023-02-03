package com.stytch.sdk.consumer.network

import androidx.annotation.VisibleForTesting
import com.squareup.moshi.Moshi
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.Constants.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.StytchAuthHeaderInterceptor
import com.stytch.sdk.consumer.OAuthAuthenticatedResponse
import com.stytch.sdk.consumer.StytchClient
import java.lang.RuntimeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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

    internal val isInitialized: Boolean
        get() {
            return ::publicToken.isInitialized && ::deviceInfo.isInitialized
        }

    internal val isTestToken: Boolean
        get() {
            StytchClient.assertInitialized()
            return publicToken.contains("public-token-test")
        }

    @VisibleForTesting
    internal val apiService: StytchApiService by lazy {
        StytchClient.assertInitialized()
        StytchApiService.Companion.createApiService(
            Constants.WEB_URL,
            authHeaderInterceptor
        )
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
            ): StytchResult<BasicData> = safeApiCall {
                apiService.loginOrCreateUserByEmail(
                    StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest(
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
            ): StytchResult<AuthData> = safeApiCall {
                apiService.authenticate(
                    StytchRequests.MagicLinks.AuthenticateRequest(
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
            ): StytchResult<BasicData> = safeApiCall {
                apiService.sendEmailMagicLinkPrimary(
                    StytchRequests.MagicLinks.SendRequest(
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
            ): StytchResult<BasicData> = safeApiCall {
                apiService.sendEmailMagicLinkSecondary(
                    StytchRequests.MagicLinks.SendRequest(
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
        ): StytchResult<LoginOrCreateOTPData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithSMS(
                StytchRequests.OTP.SMS(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSPrimary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithSMSPrimary(
                StytchRequests.OTP.SMS(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMSSecondary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithSMSSecondary(
                StytchRequests.OTP.SMS(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithWhatsApp(
            phoneNumber: String,
            expirationMinutes: UInt
        ): StytchResult<LoginOrCreateOTPData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithWhatsApp(
                StytchRequests.OTP.WhatsApp(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppPrimary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithWhatsAppPrimary(
                StytchRequests.OTP.WhatsApp(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsAppSecondary(
            phoneNumber: String,
            expirationMinutes: UInt?,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithWhatsAppSecondary(
                StytchRequests.OTP.WhatsApp(
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
        ): StytchResult<LoginOrCreateOTPData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithEmail(
                StytchRequests.OTP.Email(
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
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithEmailPrimary(
                StytchRequests.OTP.Email(
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
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithEmailSecondary(
                StytchRequests.OTP.Email(
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
        ): StytchResult<AuthData> = safeApiCall {
            apiService.authenticateWithOTP(
                StytchRequests.OTP.Authenticate(
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
        ): StytchResult<AuthData> = safeApiCall {
            apiService.authenticateWithPasswords(
                StytchRequests.Passwords.AuthenticateRequest(
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
        ): StytchResult<CreateResponse> = safeApiCall {
            apiService.passwords(
                StytchRequests.Passwords.CreateRequest(
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
            resetPasswordExpirationMinutes: Int?
        ): StytchResult<BasicData> = safeApiCall {
            apiService.resetByEmailStart(
                StytchRequests.Passwords.ResetByEmailStartRequest(
                    email,
                    codeChallenge,
                    codeChallengeMethod,
                    loginRedirectUrl,
                    loginExpirationMinutes,
                    resetPasswordRedirectUrl,
                    resetPasswordExpirationMinutes
                )
            )
        }

        suspend fun resetByEmail(
            token: String,
            password: String,
            sessionDurationMinutes: UInt,
            codeVerifier: String
        ): StytchResult<AuthData> = safeApiCall {
            apiService.resetByEmail(
                StytchRequests.Passwords.ResetByEmailRequest(
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
        ): StytchResult<StrengthCheckResponse> = safeApiCall {
            apiService.strengthCheck(
                StytchRequests.Passwords.StrengthCheckRequest(
                    email,
                    password
                )
            )
        }
    }

    internal object Sessions {

        suspend fun authenticate(
            sessionDurationMinutes: UInt? = null
        ): StytchResult<AuthData> = safeApiCall {
            apiService.authenticateSessions(
                StytchRequests.Sessions.AuthenticateRequest(
                    sessionDurationMinutes?.toInt()
                )
            )
        }

        suspend fun revoke(): StytchResult<BasicData> = safeApiCall {
            apiService.revokeSessions()
        }
    }

    internal object Biometrics {
        suspend fun registerStart(
            publicKey: String,
        ): StytchResult<BiometricsStartResponse> = safeApiCall {
            apiService.biometricsRegisterStart(
                StytchRequests.Biometrics.RegisterStartRequest(
                    publicKey = publicKey,
                )
            )
        }

        suspend fun register(
            signature: String,
            biometricRegistrationId: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<BiometricsAuthData> = safeApiCall {
            apiService.biometricsRegister(
                StytchRequests.Biometrics.RegisterRequest(
                    signature = signature,
                    biometricRegistrationId = biometricRegistrationId,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                )
            )
        }

        suspend fun authenticateStart(
            publicKey: String,
        ): StytchResult<BiometricsStartResponse> = safeApiCall {
            apiService.biometricsAuthenticateStart(
                StytchRequests.Biometrics.AuthenticateStartRequest(
                    publicKey = publicKey,
                )
            )
        }

        suspend fun authenticate(
            signature: String,
            biometricRegistrationId: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<BiometricsAuthData> = safeApiCall {
            apiService.biometricsAuthenticate(
                StytchRequests.Biometrics.AuthenticateRequest(
                    signature = signature,
                    biometricRegistrationId = biometricRegistrationId,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                )
            )
        }
    }

    internal object UserManagement {
        suspend fun getUser(): StytchResult<UserData> = safeApiCall {
            apiService.getUser()
        }

        suspend fun deleteEmailById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeApiCall {
                apiService.deleteEmailById(id)
            }

        suspend fun deletePhoneNumberById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeApiCall {
                apiService.deletePhoneNumberById(id)
            }

        suspend fun deleteBiometricRegistrationById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeApiCall {
                apiService.deleteBiometricRegistrationById(id)
            }

        suspend fun deleteCryptoWalletById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeApiCall {
                apiService.deleteCryptoWalletById(id)
            }

        suspend fun deleteWebAuthnById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeApiCall {
                apiService.deleteWebAuthnById(id)
            }
    }

    internal object OAuth {
        suspend fun authenticateWithGoogleIdToken(
            idToken: String,
            nonce: String,
            sessionDurationMinutes: UInt,
        ): StytchResult<AuthData> = safeApiCall {
            apiService.authenticateWithGoogleIdToken(
                StytchRequests.OAuth.Google.AuthenticateRequest(
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
        ): OAuthAuthenticatedResponse = safeApiCall {
            apiService.authenticateWithThirdPartyToken(
                StytchRequests.OAuth.ThirdParty.AuthenticateRequest(
                    token = token,
                    sessionDurationMinutes = sessionDurationMinutes.toInt(),
                    codeVerifier = codeVerifier
                )
            )
        }
    }

    @VisibleForTesting
    internal suspend fun <T1, T : com.stytch.sdk.consumer.network.StytchResponses.StytchDataResponse<T1>> safeApiCall(
        apiCall: suspend () -> T
    ): StytchResult<T1> =
        withContext(Dispatchers.IO) {
            StytchClient.assertInitialized()
            try {
                StytchResult.Success(apiCall().data)
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        val errorCode = throwable.code()
                        val stytchErrorResponse = try {
                            throwable.response()?.errorBody()?.source()?.let {
                                Moshi.Builder().build().adapter(StytchErrorResponse::class.java).fromJson(it)
                            }
                        } catch (t: Throwable) {
                            null
                        }
                        StytchLog.w("http error code: $errorCode, errorResponse: $stytchErrorResponse")
                        StytchResult.Error(StytchExceptions.Response(stytchErrorResponse))
                    }
                    is StytchExceptions -> {
                        StytchResult.Error(throwable)
                    }
                    else -> {
                        throwable.printStackTrace()
                        StytchLog.w("Network Error")
                        StytchResult.Error(StytchExceptions.Connection(throwable))
                    }
                }
            }
        }
}

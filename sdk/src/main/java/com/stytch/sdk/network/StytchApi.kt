package com.stytch.sdk.network

import androidx.annotation.VisibleForTesting
import com.squareup.moshi.Moshi
import com.stytch.sdk.Constants
import com.stytch.sdk.Constants.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.DeviceInfo
import com.stytch.sdk.OAuthAuthenticatedResponse
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.StytchLog
import com.stytch.sdk.StytchResult
import com.stytch.sdk.UserAttributes
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.BasicData
import com.stytch.sdk.network.responseData.BiometricsAuthData
import com.stytch.sdk.network.responseData.BiometricsStartResponse
import com.stytch.sdk.network.responseData.CreateResponse
import com.stytch.sdk.network.responseData.DeleteAuthenticationFactorData
import com.stytch.sdk.network.responseData.LoginOrCreateOTPData
import com.stytch.sdk.network.responseData.StrengthCheckResponse
import com.stytch.sdk.network.responseData.StytchErrorResponse
import com.stytch.sdk.network.responseData.UserData
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
            publicToken
        )
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
        StytchApiService.createApiService(Constants.WEB_URL, authHeaderInterceptor)
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
            suspend fun send(
                email: String,
                loginMagicLinkUrl: String?,
                signupMagicLinkUrl: String?,
                loginExpirationMinutes: Int?,
                signupExpirationMinutes: Int?,
                loginTemplateId: String?,
                signupTemplateId: String?,
                locale: String?,
                attributes: UserAttributes?,
                codeChallenge: String?,
                userId: String?,
                sessionToken: String?,
                sessionJwt: String?,
            ): StytchResult<BasicData> = safeApiCall {
                apiService.sendEmailMagicLink(
                    StytchRequests.MagicLinks.SendRequest(
                        email = email,
                        loginMagicLinkUrl = loginMagicLinkUrl,
                        signupMagicLinkUrl = signupMagicLinkUrl,
                        loginExpirationMinutes = loginExpirationMinutes,
                        signupExpirationMinutes = signupExpirationMinutes,
                        loginTemplateId = loginTemplateId,
                        signupTemplateId = signupTemplateId,
                        locale = locale,
                        attributes = attributes?.toApiAttributes(),
                        codeChallenge = codeChallenge,
                        userId = userId,
                        sessionToken = sessionToken,
                        sessionJwt = sessionJwt,
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
                StytchRequests.OTP.SMS.LoginOrCreateRequest(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithSMS(
            phoneNumber: String,
            expirationMinutes: UInt?,
            locale: String?,
            attributes: UserAttributes?,
            userId: String?,
            sessionToken: String?,
            sessionJwt: String?,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithSMS(
                StytchRequests.OTP.SMS.SendRequest(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                    locale = locale,
                    attributes = attributes?.toApiAttributes(),
                    userId = userId,
                    sessionToken = sessionToken,
                    sessionJwt = sessionJwt,
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithWhatsApp(
            phoneNumber: String,
            expirationMinutes: UInt
        ): StytchResult<LoginOrCreateOTPData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithWhatsApp(
                StytchRequests.OTP.WhatsApp.LoginOrCreateRequest(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithWhatsApp(
            phoneNumber: String,
            expirationMinutes: UInt?,
            locale: String?,
            attributes: UserAttributes?,
            userId: String?,
            sessionToken: String?,
            sessionJwt: String?,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithWhatsApp(
                StytchRequests.OTP.WhatsApp.SendRequest(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes?.toInt(),
                    locale = locale,
                    attributes = attributes?.toApiAttributes(),
                    userId = userId,
                    sessionToken = sessionToken,
                    sessionJwt = sessionJwt,
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
                StytchRequests.OTP.Email.LoginOrCreateRequest(
                    email = email,
                    expirationMinutes = expirationMinutes.toInt(),
                    loginTemplateId = loginTemplateId,
                    signupTemplateId = signupTemplateId,
                )
            )
        }

        @Suppress("LongParameterList")
        suspend fun sendOTPWithEmail(
            email: String,
            expirationMinutes: UInt?,
            loginTemplateId: String?,
            signupTemplateId: String?,
            locale: String?,
            attributes: UserAttributes?,
            userId: String?,
            sessionToken: String?,
            sessionJwt: String?,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.sendOTPWithEmail(
                StytchRequests.OTP.Email.SendRequest(
                    email = email,
                    expirationMinutes = expirationMinutes?.toInt(),
                    loginTemplateId = loginTemplateId,
                    signupTemplateId = signupTemplateId,
                    locale = locale,
                    attributes = attributes?.toApiAttributes(),
                    userId = userId,
                    sessionToken = sessionToken,
                    sessionJwt = sessionJwt,
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
            sessionDurationMinutes: Int? = null
        ): StytchResult<AuthData> = safeApiCall {
            apiService.authenticateSessions(
                StytchRequests.Sessions.AuthenticateRequest(
                    sessionDurationMinutes
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

        suspend fun deleteEmailById(id: String): StytchResult<DeleteAuthenticationFactorData> = safeApiCall {
            apiService.deleteEmailById(id)
        }

        suspend fun deletePhoneNumberById(id: String): StytchResult<DeleteAuthenticationFactorData> = safeApiCall {
            apiService.deletePhoneNumberById(id)
        }

        suspend fun deleteBiometricRegistrationById(id: String): StytchResult<DeleteAuthenticationFactorData> =
            safeApiCall {
                apiService.deleteBiometricRegistrationById(id)
            }

        suspend fun deleteCryptoWalletById(id: String): StytchResult<DeleteAuthenticationFactorData> = safeApiCall {
            apiService.deleteCryptoWalletById(id)
        }

        suspend fun deleteWebAuthnById(id: String): StytchResult<DeleteAuthenticationFactorData> = safeApiCall {
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
    internal suspend fun <T1, T : StytchResponses.StytchDataResponse<T1>> safeApiCall(
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

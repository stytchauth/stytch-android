package com.stytch.sdk.network

import androidx.annotation.VisibleForTesting
import com.squareup.moshi.Moshi
import com.stytch.sdk.Constants
import com.stytch.sdk.Constants.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.DeviceInfo
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.StytchLog
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.BasicData
import com.stytch.sdk.network.responseData.CreateResponse
import com.stytch.sdk.network.responseData.LoginOrCreateOTPData
import com.stytch.sdk.network.responseData.StrengthCheckResponse
import com.stytch.sdk.network.responseData.StytchErrorResponse
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val ONE_HUNDRED_TWENTY = 120L
private const val HTTP_UNAUTHORIZED = 401

internal object StytchApi {

    private lateinit var publicToken: String
    private lateinit var deviceInfo: DeviceInfo
    internal var hostUrl: String = Constants.HOST_URL

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
            return ::publicToken.isInitialized &&
                ::deviceInfo.isInitialized
        }

    @VisibleForTesting
    internal val apiService: StytchApiService by lazy {
        StytchClient.assertInitialized()
        createApiService(hostUrl)
    }

    private fun createApiService(hostUrl: String): StytchApiService {
        return Retrofit.Builder()
            .baseUrl(hostUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
                    .writeTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
                    .connectTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
                    .addInterceptor(authHeaderInterceptor)
                    .addInterceptor(
                        Interceptor { chain ->
                            val request = chain.request()
                            val response = chain.proceed(request)
                            if (response.code == HTTP_UNAUTHORIZED) {
                                StytchClient.sessionStorage.revoke()
                            }
                            return@Interceptor response
                        }
                    )
                    .addNetworkInterceptor {
                        // OkHttp is adding a charset to the content-type which is rejected by the API
                        // see: https://github.com/square/okhttp/issues/3081
                        it.proceed(
                            it.request()
                                .newBuilder()
                                .header("Content-Type", "application/json")
                                .build()
                        )
                    }
                    .build()
            )
            .build()
            .create(StytchApiService::class.java)
    }

    internal object MagicLinks {
        object Email {
            /** https://stytch.com/docs/api/log-in-or-create-user-by-email */
            suspend fun loginOrCreate(
                email: String,
                loginMagicLinkUrl: String?,
                codeChallenge: String,
                codeChallengeMethod: String
            ): StytchResult<BasicData> = safeApiCall {
                apiService.loginOrCreateUserByEmail(
                    StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest(
                        email = email,
                        loginMagicLinkUrl = loginMagicLinkUrl,
                        codeChallenge = codeChallenge,
                        codeChallengeMethod = codeChallengeMethod
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

        suspend fun loginOrCreateUserByOTPWithEmail(
            email: String,
            expirationMinutes: UInt
        ): StytchResult<LoginOrCreateOTPData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithEmail(
                StytchRequests.OTP.Email(
                    email = email,
                    expirationMinutes = expirationMinutes.toInt()
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

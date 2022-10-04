package com.stytch.sdk.network

import com.squareup.moshi.Moshi
import com.stytch.sdk.Constants
import com.stytch.sdk.DeviceInfo
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchLog
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.BasicData
import com.stytch.sdk.network.responseData.CreateResponse
import com.stytch.sdk.network.responseData.StrengthCheckResponse
import com.stytch.sdk.network.responseData.StytchErrorResponse
import com.stytch.sdk.network.responseData.UserData
import com.stytch.sdk.stytchError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

internal object StytchApi {

    private lateinit var publicToken: String
    private lateinit var hostUrl: String
    private lateinit var deviceInfo: DeviceInfo

    //save reference for changing auth header
    //make sure api is configured before accessing this variable
    private val authHeaderInterceptor: StytchAuthHeaderInterceptor by lazy {
        if (!isInitialized) {
            stytchError("StytchApi not configured. You must call 'StytchApi.configure(...)' before using any functionality of the StytchApi.")
        }
        StytchAuthHeaderInterceptor(
            deviceInfo,
            publicToken
        )
    }

    internal fun configure(publicToken: String, hostUrl: String, deviceInfo: DeviceInfo) {
        this.publicToken = publicToken
        this.hostUrl = hostUrl
        this.deviceInfo = deviceInfo
    }

    internal val isInitialized: Boolean
        get() {
            return ::publicToken.isInitialized
                    && ::hostUrl.isInitialized
                    && ::deviceInfo.isInitialized
        }

    private val apiService: StytchApiService by lazy {
        StytchClient.assertInitialized()
        Retrofit.Builder()
            .baseUrl(hostUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(120L, TimeUnit.SECONDS)
                    .writeTimeout(120L, TimeUnit.SECONDS)
                    .connectTimeout(120L, TimeUnit.SECONDS)
                    .addInterceptor(authHeaderInterceptor)
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
                codeChallengeMethod: String,
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

            suspend fun authenticate(token: String, sessionDurationMinutes: UInt = Constants.DEFAULT_SESSION_TIME_MINUTES, codeVerifier: String):
                    StytchResult<AuthData> =
                safeApiCall {
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
            expirationMinutes: UInt,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithSMS(
                StytchRequests.OTP.SMS(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithWhatsApp(
            phoneNumber: String,
            expirationMinutes: UInt,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithWhatsApp(
                StytchRequests.OTP.WhatsApp(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithEmail(
            email: String,
            expirationMinutes: UInt,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithEmail(
                StytchRequests.OTP.Email(
                    email = email,
                    expirationMinutes = expirationMinutes.toInt()
                )
            )
        }

        suspend fun authenticateWithOTP(token: String, sessionDurationMinutes: UInt = 60u):
                StytchResult<AuthData> =
            safeApiCall {
                apiService.authenticateWithOTP(
                    StytchRequests.OTP.Authenticate(
                        token,
                        sessionDurationMinutes.toInt()
                    )
                )
            }
    }

    internal object Passwords {

        suspend fun authenticate(
            email: String,
            password: String,
            sessionDurationMinutes: Int,
        ): StytchResult<AuthData> = safeApiCall {
            apiService.authenticateWithPasswords(
                StytchRequests.Passwords.AuthenticateRequest(
                    email,
                    password,
                    sessionDurationMinutes
                )
            )
        }

        suspend fun create(
            email: String,
            password: String,
            sessionDurationMinutes: Int,
        ): StytchResult<CreateResponse> = safeApiCall {
            apiService.passwords(
                StytchRequests.Passwords.CreateRequest(
                    email,
                    password,
                    sessionDurationMinutes
                )
            )
        }

        suspend fun resetByEmailStart(
            email: String,
            codeChallenge: String,
            codeChallengeMethod: String,
            loginRedirectUrl: String?,
            loginExpirationMinutes: Int?,
            resetPasswordRedirectUrl: String?,
            resetPasswordExpirationMinutes: Int?,
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
            sessionDurationMinutes: Int,
            codeVerifier: String,
        ): StytchResult<AuthData> = safeApiCall {
            apiService.resetByEmail(
                StytchRequests.Passwords.RestByEmailRequest(
                    token,
                    password,
                    sessionDurationMinutes,
                    codeVerifier
                )
            )
        }

        suspend fun strengthCheck(
             email: String?,
             password: String,
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

        suspend fun revoke():
                StytchResult<BasicData> =
            safeApiCall {
                apiService.revokeSessions()
            }
    }

    internal object UserManagement {

        suspend fun getUser(): StytchResult<UserData> = safeApiCall {
            apiService.getUser()
        }

        public suspend fun deleteEmailById(id: String): StytchResult<BasicData> {
            return  safeApiCall {
                apiService.deleteEmailById(id)
            }
        }

        public suspend fun deletePhoneNumberById(id: String): StytchResult<BasicData>{
            return  safeApiCall {
                apiService.deletePhoneNumberById(id)
            }
        }

        public suspend fun deleteBiometricRegistrationById(id: String): StytchResult<BasicData>{
            return  safeApiCall {
                apiService.deleteBiometricRegistrationById(id)
            }
        }



    }

    private suspend fun <T1, T : StytchResponses.StytchDataResponse<T1>> safeApiCall(apiCall: suspend () -> T): StytchResult<T1> =
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
                        StytchResult.Error(errorCode = errorCode, errorResponse = stytchErrorResponse)
                    }
                    else -> {
                        throwable.printStackTrace()
                        StytchLog.w("Network Error")
                        StytchResult.NetworkError
                    }
                }
            }
        }
}

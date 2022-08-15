package com.stytch.sdk.network

import com.squareup.moshi.Moshi
import com.stytch.sdk.DeviceInfo
import com.stytch.sdk.StytchClient
import com.stytch.sdk.StytchLog
import com.stytch.sdk.StytchResult
import com.stytch.sdk.network.responseData.BasicData
import com.stytch.sdk.network.responseData.StytchErrorResponse
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
    private var stytchSessionToken: String? = null

    //save reference for changing auth header
    //make sure api is configured before accessing this variable
    private val authHeaderInterceptor: StytchAuthHeaderInterceptor by lazy {
        if (!isInitialized) {
            stytchError("StytchApi not configured. You must call 'StytchApi.configure(...)' before using any functionality of the StytchApi.")
        }
        StytchAuthHeaderInterceptor(
            deviceInfo,
            publicToken,
            stytchSessionToken
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
            suspend fun loginOrCreateEmail(
                email: String,
                loginMagicLinkUrl: String?,
                codeChallenge: String,
                codeChallengeMethod: String,
            ): StytchResult<BasicData> = safeApiCall {
                apiService.loginOrCreateUserByEmail(
                    StytchRequests.MagicLinks.Email.LoginOrCreateUserByEmailRequest(
                        email = email,
                        login_magic_link_url = loginMagicLinkUrl,
                        code_challenge = codeChallenge,
                        code_challenge_method = codeChallengeMethod
                    )
                )
            }

            suspend fun authenticate(token: String, sessionDurationInMinutes: UInt = 60u, codeVerifier: String):
                    StytchResult<BasicData> =
                safeApiCall {
                    apiService.authenticate(
                        StytchRequests.MagicLinks.Authenticate(
                            token,
                            codeVerifier,
                            sessionDurationInMinutes.toInt()
                        )
                    )
                }
        }
    }

    internal object OTP {
        suspend fun loginOrCreateByOTPWithSMS(
            phoneNumber: String,
            expirationInMinutes: UInt,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithSMS(
                StytchRequests.OTP.SMS(
                    phone_number = phoneNumber,
                    expiration_minutes = expirationInMinutes.toInt()
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithWhatsapp(
            phoneNumber: String,
            expirationInMinutes: UInt,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithWhatsapp(
                StytchRequests.OTP.Whatsapp(
                    phone_number = phoneNumber,
                    expiration_minutes = expirationInMinutes.toInt()
                )
            )
        }

        suspend fun loginOrCreateUserByOTPWithEmail(
            email: String,
            expirationInMinutes: UInt,
        ): StytchResult<BasicData> = safeApiCall {
            apiService.loginOrCreateUserByOTPWithEmail(
                StytchRequests.OTP.Email(
                    email = email,
                    expiration_minutes = expirationInMinutes.toInt()
                )
            )
        }

        suspend fun authenticateWithOTP(token: String, sessionDurationInMinutes: UInt = 60u):
                StytchResult<BasicData> =
            safeApiCall {
                apiService.authenticateWithOTP(
                    StytchRequests.OTP.Authenticate(
                        token,
                        sessionDurationInMinutes.toInt()
                    )
                )
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

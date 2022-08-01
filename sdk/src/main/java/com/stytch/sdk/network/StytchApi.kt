package com.stytch.sdk.network

import com.squareup.moshi.Moshi
import com.stytch.sdk.DeviceInfo
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchDataTypes
import com.stytch.sdk.StytchErrorResponse
import com.stytch.sdk.StytchLog
import com.stytch.sdk.StytchResult
import com.stytch.sdk.stytchError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        Stytch.assertInitialized()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        Retrofit.Builder()
            .baseUrl(hostUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(120L, TimeUnit.SECONDS)
                    .writeTimeout(120L, TimeUnit.SECONDS)
                    .connectTimeout(120L, TimeUnit.SECONDS)
                    .addInterceptor(authHeaderInterceptor)
                    .addInterceptor(loggingInterceptor)
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
                signupMagicLinkUrl: String?,
                loginExpirationMinutes: Int? = null,
                signupExpirationMinutes: Int? = null,
                createUserAsPending: Boolean? = null,
                attributes: StytchDataTypes.Attributes? = null,
            ): StytchResult<StytchResponses.LoginOrCreateUserByEmailResponse> = safeApiCall {
                apiService.loginOrCreateUserByEmail(
                    StytchRequests.LoginOrCreateUserByEmailRequest(
                        email = email,
                        login_magic_link_url = loginMagicLinkUrl,
                        signup_magic_link_url = signupMagicLinkUrl,
                        login_expiration_minutes = loginExpirationMinutes,
                        signup_expiration_minutes = signupExpirationMinutes,
                        create_user_as_pending = createUserAsPending,
                        attributes = attributes,
                    )
                )
            }

            suspend fun authenticate(token: String, sessionDurationMinutes: Int = 60):
                    StytchResult<StytchResponses.BasicResponse> =
                safeApiCall {
                    apiService.authenticate(
                        StytchRequests.Authenticate(
                            token,
                            sessionDurationMinutes
                        )
                    )
                }

        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): StytchResult<T> = withContext(Dispatchers.IO) {
        Stytch.assertInitialized()
        try {
            StytchResult.Success(apiCall())
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


package com.stytch.sdk

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

public object StytchApi {
    public object MagicLinks {
        public object Email {
            public suspend fun loginOrCreateUser(
                email: String,
                loginMagicLinkUrl: String,
                signupMagicLinkUrl: String,
                loginExpirationMinutes: Int? = null,
                signupExpirationMinutes: Int? = null,
                createUserAsPending: Boolean? = null,
                attributes: StytchDataTypes.Attributes? = null,
            ): StytchResult<StytchResponseTypes.LoginOrCreateUserByEmailResponse> = safeApiCall {
                apiService.loginOrCreateUserByEmail(
                    StytchRequestTypes.SDKLoginOrCreateUserByEmailRequest(
                        public_token = Stytch.publicToken,
                        request = StytchRequestTypes.LoginOrCreateUserByEmailRequest(
                            email = email,
                            login_magic_link_url = loginMagicLinkUrl,
                            signup_magic_link_url = signupMagicLinkUrl,
                            login_expiration_minutes = loginExpirationMinutes,
                            signup_expiration_minutes = signupExpirationMinutes,
                            create_user_as_pending = createUserAsPending,
                            attributes = attributes,
                        )
                    )
                )
            }
        }
    }

    public object OTPs {
        public object SMS {
            public suspend fun loginOrCreateUser(
                phoneNumber: String,
                expirationMinutes: Int? = null,
                createUserAsPending: Boolean? = null,
                attributes: StytchDataTypes.Attributes? = null,
            ): StytchResult<StytchResponseTypes.LoginOrCreateUserBySMSResponse> = safeApiCall {
                apiService.loginOrCreateUserBySMS(
                    StytchRequestTypes.SDKLoginOrCreateUserBySMSRequest(
                        public_token = Stytch.publicToken,
                        request = StytchRequestTypes.LoginOrCreateUserBySMSRequest(
                            phone_number = phoneNumber,
                            expiration_minutes = expirationMinutes,
                            create_user_as_pending = createUserAsPending,
                            attributes = attributes,
                        )
                    )
                )
            }
        }
    }

    internal val apiService by lazy {
        Stytch.assertInitialized()
        Retrofit.Builder()
            .baseUrl(Stytch.environment.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(120L, TimeUnit.SECONDS)
                    .writeTimeout(120L, TimeUnit.SECONDS)
                    .connectTimeout(120L, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        chain.proceed(
                            chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .build()
                        )
                    }
                    .build()
            )
            .build()
            .create(StytchApiService::class.java)
    }
}

public object StytchCallbackApi {
    public object MagicLinks {
        public object Email {
            @JvmStatic
            public fun loginOrCreateUser(
                email: String,
                loginMagicLinkUrl: String,
                signupMagicLinkUrl: String,
                loginExpirationMinutes: Int? = null,
                signupExpirationMinutes: Int? = null,
                createUserAsPending: Boolean? = null,
                attributes: StytchDataTypes.Attributes? = null,
                callback: (StytchResult<StytchResponseTypes.LoginOrCreateUserByEmailResponse>) -> Unit,
            ): Unit = callback.queue {
                StytchApi.MagicLinks.Email.loginOrCreateUser(
                    email = email,
                    loginMagicLinkUrl = loginMagicLinkUrl,
                    signupMagicLinkUrl = signupMagicLinkUrl,
                    loginExpirationMinutes = loginExpirationMinutes,
                    signupExpirationMinutes = signupExpirationMinutes,
                    createUserAsPending = createUserAsPending,
                    attributes = attributes,
                )
            }
        }
    }

    public object OTPs {
        public object SMS {
            @JvmStatic
            public fun loginOrCreateUser(
                phoneNumber: String,
                expirationMinutes: Int? = null,
                createUserAsPending: Boolean? = null,
                attributes: StytchDataTypes.Attributes? = null,
                callback: (StytchResult<StytchResponseTypes.LoginOrCreateUserBySMSResponse>) -> Unit,
            ): Unit = callback.queue {
                StytchApi.OTPs.SMS.loginOrCreateUser(
                    phoneNumber = phoneNumber,
                    expirationMinutes = expirationMinutes,
                    createUserAsPending = createUserAsPending,
                    attributes = attributes,
                )
            }
        }
    }

    private fun <T> ((T) -> Unit).queue(block: suspend () -> T) {
        GlobalScope.launch {
            val result = block()
            withContext(Dispatchers.Main) {
                this@queue(result)
            }
        }
    }
}

public object StytchDataTypes {
    @JsonClass(generateAdapter = true)
    public data class Attributes(
        val ip_address: String?,
        val user_agent: String?,
    )
}

internal object StytchRequestTypes {
    @JsonClass(generateAdapter = true)
    data class LoginOrCreateUserByEmailRequest(
        val email: String,
        val login_magic_link_url: String,
        val signup_magic_link_url: String,
        val login_expiration_minutes: Int?,
        val signup_expiration_minutes: Int?,
        val create_user_as_pending: Boolean?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    data class SDKLoginOrCreateUserByEmailRequest(
        val public_token: String,
        val request: LoginOrCreateUserByEmailRequest,
    )

    @JsonClass(generateAdapter = true)
    data class LoginOrCreateUserBySMSRequest(
        val phone_number: String,
        val expiration_minutes: Int?,
        val create_user_as_pending: Boolean?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    data class SDKLoginOrCreateUserBySMSRequest(
        val public_token: String,
        val request: LoginOrCreateUserBySMSRequest,
    )
}

public object StytchResponseTypes {
    @JsonClass(generateAdapter = true)
    public data class LoginOrCreateUserByEmailResponse(
        val request_id: String,
        val user_id: String,
        val email_id: String,
        val user_created: Boolean,
    )

    @JsonClass(generateAdapter = true)
    public data class LoginOrCreateUserBySMSResponse(
        val request_id: String,
        val user_id: String,
        val phone_id: String,
    )
}

@JsonClass(generateAdapter = true)
public data class StytchErrorResponse(
    val status_code: Int,
    val request_id: String,
    val error_type: String,
    val error_message: String?,
    val error_url: String,
)

public enum class StytchErrorType(public val stringValue: String) {
    EMAIL_NOT_FOUND("email_not_found"),
    BILLING_NOT_VERIFIED_FOR_EMAIL("billing_not_verified_for_email"),
    UNABLE_TO_AUTH_MAGIC_LINK("unable_to_auth_magic_link"),
    INVALID_USER_ID("invalid_user_id"),
    UNAUTHORIZED_CREDENTIALS("unauthorized_credentials"),
    INTERNAL_SERVER_ERROR("internal_server_error"),
    TOO_MANY_REQUESTS("too_many_requests"),
    INVALID_PHONE_NUMBER("invalid_phone_number"),
    UNABLE_TO_AUTH_OTP_CODE("unable_to_auth_otp_code"),
    OTP_CODE_NOT_FOUND("otp_code_not_found"),
}

internal fun String.toStytchErrorType(): StytchErrorType? {
    StytchErrorType.values().forEach {
        if (this == it.stringValue) return it
    }
    return null
}

internal interface StytchApiService {
    @POST("sdk/magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequestTypes.SDKLoginOrCreateUserByEmailRequest,
    ): StytchResponseTypes.LoginOrCreateUserByEmailResponse

    @POST("sdk/otps/sms/login_or_create")
    suspend fun loginOrCreateUserBySMS(
        @Body request: StytchRequestTypes.SDKLoginOrCreateUserBySMSRequest,
    ): StytchResponseTypes.LoginOrCreateUserBySMSResponse
}

public sealed class StytchResult<out T> {
    public data class Success<out T>(val value: T) : StytchResult<T>()
    public object NetworkError : StytchResult<Nothing>()
    public data class Error(val errorCode: Int, val errorResponse: StytchErrorResponse?) : StytchResult<Nothing>() {
        public val errorType: StytchErrorType? by lazy {
            errorResponse?.error_type?.toStytchErrorType()
        }
    }
}

internal suspend fun <T> safeApiCall(apiCall: suspend () -> T): StytchResult<T> = withContext(Dispatchers.IO) {
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
                StytchLog.w("Network Error")
                StytchResult.NetworkError
            }
        }
    }
}

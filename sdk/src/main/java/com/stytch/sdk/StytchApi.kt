package com.stytch.sdk

import android.util.Log
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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.io.Serializable
import java.util.concurrent.TimeUnit

public object StytchApi {
    public object Users {
        public suspend fun createUser(
            email: String? = null,
            phoneNumber: String? = null,
            name: StytchDataTypes.Name? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.CreateUserResponse> = safeApiCall {
            apiService.createUser(
                StytchRequestTypes.CreateUserRequest(
                    email = email,
                    phone_number = phoneNumber,
                    name = name,
                    attributes = attributes,
                )
            )
        }

        public suspend fun getUser(
            userId: String,
        ): StytchResult<StytchResponseTypes.GetUserResponse> = safeApiCall {
            apiService.getUser(
                userId = userId,
            )
        }

        public suspend fun updateUser(
            userId: String,
            name: StytchDataTypes.Name? = null,
            email: String? = null,
            phoneNumber: String? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.UpdateUserResponse> = safeApiCall {
            apiService.updateUser(
                userId = userId,
                request = StytchRequestTypes.UpdateUserRequest(
                    name = name,
                    emails = if (email == null) null else StytchDataTypes.Emails(email),
                    phone_numbers = if (phoneNumber == null) null else StytchDataTypes.PhoneNumbers(phoneNumber),
                    attributes = attributes,
                )
            )
        }

        public suspend fun deleteUser(
            userId: String,
        ): StytchResult<StytchResponseTypes.DeleteUserResponse> = safeApiCall {
            apiService.deleteUser(
                userId = userId,
            )
        }

        public suspend fun deleteUserEmail(
            userId: String,
        ): StytchResult<StytchResponseTypes.DeleteUserEmailResponse> = safeApiCall {
            apiService.deleteUserEmail(
                userId = userId,
            )
        }

        public suspend fun deleteUserPhoneNumber(
            userId: String,
        ): StytchResult<StytchResponseTypes.DeleteUserPhoneNumberResponse> = safeApiCall {
            apiService.deleteUserPhoneNumber(
                userId = userId,
            )
        }

        public suspend fun getPendingUsers(
            limit: Int? = null,
            startingAfterId: String? = null,
        ): StytchResult<StytchResponseTypes.GetPendingUsersResponse> = safeApiCall {
            apiService.getPendingUsers(
                StytchRequestTypes.GetPendingUsersRequest(
                    limit = limit,
                    starting_after_id = startingAfterId,
                )
            )
        }
    }

    public object MagicLinks {
        public suspend fun sendMagicLink(
            userId: String,
            methodId: String,
            magicLinkUrl: String,
            expirationMinutes: Int? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.SendMagicLinkResponse> = safeApiCall {
            apiService.sendMagicLink(
                StytchRequestTypes.SendMagicLinkRequest(
                    user_id = userId,
                    method_id = methodId,
                    magic_link_url = magicLinkUrl,
                    expiration_minutes = expirationMinutes,
                    attributes = attributes,
                )
            )
        }

        public suspend fun sendMagicLinkByEmail(
            email: String,
            magicLinkUrl: String,
            expirationMinutes: Int? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.SendMagicLinkByEmailResponse> = safeApiCall {
            apiService.sendMagicLinkByEmail(
                StytchRequestTypes.SendMagicLinkByEmailRequest(
                    email = email,
                    magic_link_url = magicLinkUrl,
                    expiration_minutes = expirationMinutes,
                    attributes = attributes,
                )
            )
        }

        public suspend fun loginOrCreateUserByEmail(
            email: String,
            loginMagicLinkUrl: String,
            signupMagicLinkUrl: String,
            loginExpirationMinutes: Int? = null,
            signupExpirationMinutes: Int? = null,
            createUserAsPending: Boolean? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.LoginOrCreateUserByEmailResponse> = safeApiCall {
            apiService.loginOrCreateUserByEmail(
                StytchRequestTypes.LoginOrCreateUserByEmailRequest(
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

        public suspend fun inviteByEmail(
            email: String,
            magicLinkUrl: String,
            expirationMinutes: Int? = null,
            name: StytchDataTypes.Name? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.InviteByEmailResponse> = safeApiCall {
            apiService.inviteByEmail(
                StytchRequestTypes.InviteByEmailRequest(
                    email = email,
                    magic_link_url = magicLinkUrl,
                    expiration_minutes = expirationMinutes,
                    name = name,
                    attributes = attributes,
                )
            )
        }

        public suspend fun authenticateMagicLink(
            token: String,
            options: StytchDataTypes.Options? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.AuthenticateUserResponse> = safeApiCall {
            apiService.authenticateMagicLink(
                token = token,
                StytchRequestTypes.AuthenticateMagicLinkRequest(
                    options = options,
                    attributes = attributes,
                )
            )
        }

        public suspend fun revokeAPendingInvite(
            email: String,
        ): StytchResult<StytchResponseTypes.RevokeAPendingInviteResponse> = safeApiCall {
            apiService.revokeAPendingInvite(
                StytchRequestTypes.RevokeAPendingInviteRequest(
                    email = email,
                )
            )
        }
    }

    public object OTP {
        public suspend fun sendOneTimePasscodeBySMS(
            phoneNumber: String,
            expirationMinutes: Int? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.SendOneTimePasscodeBySMSResponse> = safeApiCall {
            apiService.sendOneTimePasscodeBySMS(
                StytchRequestTypes.SendOneTimePasscodeBySMSRequest(
                    phone_number = phoneNumber,
                    expiration_minutes = expirationMinutes,
                    attributes = attributes,
                )
            )
        }

        public suspend fun loginOrCreateUserBySMS(
            phoneNumber: String,
            expirationMinutes: Int? = null,
            createUserAsPending: Boolean? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.LoginOrCreateUserBySMSResponse> = safeApiCall {
            apiService.loginOrCreateUserBySMS(
                StytchRequestTypes.LoginOrCreateUserBySMSRequest(
                    phone_number = phoneNumber,
                    expiration_minutes = expirationMinutes,
                    create_user_as_pending = createUserAsPending,
                    attributes = attributes,
                )
            )
        }

        public suspend fun authenticateOneTimePasscode(
            methodId: String,
            code: String,
            options: StytchDataTypes.Options? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.AuthenticateUserResponse> = safeApiCall {
            apiService.authenticateOneTimePasscode(
                StytchRequestTypes.AuthenticateOneTimePasscodeRequest(
                    method_id = methodId,
                    code = code,
                    options = options,
                    attributes = attributes,
                )
            )
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
                                .addHeader("Authorization", Stytch.authorizationHeader)
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
    // TODO add remaining endpoints (?)

    public object OTP {
        public fun sendOneTimePasscodeBySMS(
            phoneNumber: String,
            expirationMinutes: Int? = null,
            attributes: StytchDataTypes.Attributes? = null,
            callback: (StytchResult<StytchResponseTypes.SendOneTimePasscodeBySMSResponse>) -> Unit,
        ): Unit = callback.queue {
            StytchApi.OTP.sendOneTimePasscodeBySMS(
                phoneNumber = phoneNumber,
                expirationMinutes = expirationMinutes,
                attributes = attributes,
            )
        }

        public fun loginOrCreateUserBySMS(
            phoneNumber: String,
            expirationMinutes: Int? = null,
            createUserAsPending: Boolean? = null,
            attributes: StytchDataTypes.Attributes? = null,
            callback: (StytchResult<StytchResponseTypes.LoginOrCreateUserBySMSResponse>) -> Unit,
        ): Unit = callback.queue {
            StytchApi.OTP.loginOrCreateUserBySMS(
                phoneNumber = phoneNumber,
                expirationMinutes = expirationMinutes,
                createUserAsPending = createUserAsPending,
                attributes = attributes,
            )
        }

        public fun authenticateOneTimePasscode(
            methodId: String,
            code: String,
            options: StytchDataTypes.Options? = null,
            attributes: StytchDataTypes.Attributes? = null,
            callback: (StytchResult<StytchResponseTypes.AuthenticateUserResponse>) -> Unit,
        ): Unit = callback.queue {
            StytchApi.OTP.authenticateOneTimePasscode(
                methodId = methodId,
                code = code,
                options = options,
                attributes = attributes,
            )
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
    public data class Name(
        val first_name: String?,
        val middle_name: String?,
        val last_name: String?,
    )

    @JsonClass(generateAdapter = true)
    public data class Attributes(
        val ip_address: String?,
        val user_agent: String?,
    )

    @JsonClass(generateAdapter = true)
    public data class Emails(
        val email: String?,
    )

    @JsonClass(generateAdapter = true)
    public data class PhoneNumbers(
        val phone_number: String?,
    )

    @JsonClass(generateAdapter = true)
    public data class Options(
        val ip_match_required: Boolean?,
        val user_agent_match_required: Boolean?,
    )

    @JsonClass(generateAdapter = true)
    public data class User(
        val user_id: String,
        val name: Name?,
        val emails: List<Emails>?,
        val phone_numbers: List<PhoneNumbers>?,
        val status: String,
        val invited_at: String?,
    )
}

public object StytchRequestTypes {
    @JsonClass(generateAdapter = true)
    public data class CreateUserRequest(
        val email: String?,
        val phone_number: String?,
        val name: StytchDataTypes.Name?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class UpdateUserRequest(
        val name: StytchDataTypes.Name?,
        val emails: StytchDataTypes.Emails?,
        val phone_numbers: StytchDataTypes.PhoneNumbers?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class GetPendingUsersRequest(
        val limit: Int?,
        val starting_after_id: String?,
    )

    @JsonClass(generateAdapter = true)
    public data class SendMagicLinkRequest(
        val user_id: String,
        val method_id: String,
        val magic_link_url: String,
        val expiration_minutes: Int?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class SendMagicLinkByEmailRequest(
        val email: String,
        val magic_link_url: String,
        val expiration_minutes: Int?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class LoginOrCreateUserByEmailRequest(
        val email: String,
        val login_magic_link_url: String,
        val signup_magic_link_url: String,
        val login_expiration_minutes: Int?,
        val signup_expiration_minutes: Int?,
        val create_user_as_pending: Boolean?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class InviteByEmailRequest(
        val email: String,
        val magic_link_url: String,
        val expiration_minutes: Int?,
        val name: StytchDataTypes.Name?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class AuthenticateMagicLinkRequest(
        val options: StytchDataTypes.Options?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class RevokeAPendingInviteRequest(
        val email: String,
    )

    @JsonClass(generateAdapter = true)
    public data class SendOneTimePasscodeBySMSRequest(
        val phone_number: String,
        val expiration_minutes: Int?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class LoginOrCreateUserBySMSRequest(
        val phone_number: String,
        val expiration_minutes: Int?,
        val create_user_as_pending: Boolean?,
        val attributes: StytchDataTypes.Attributes?,
    )

    @JsonClass(generateAdapter = true)
    public data class AuthenticateOneTimePasscodeRequest(
        val method_id: String,
        val code: String,
        val options: StytchDataTypes.Options?,
        val attributes: StytchDataTypes.Attributes?,
    )
}

public object StytchResponseTypes {
    @JsonClass(generateAdapter = true)
    public data class CreateUserResponse(
        val request_id: String,
        val user_id: String,
        val email_id: String?,
        val phone_id: String?,
        val status: String,
    )

    @JsonClass(generateAdapter = true)
    public data class GetUserResponse(
        val request_id: String,
        val user_id: String,
        val name: StytchDataTypes.Name?,
        val emails: StytchDataTypes.Emails?,
        val phone_numbers: StytchDataTypes.PhoneNumbers?,
        val status: String,
    )

    @JsonClass(generateAdapter = true)
    public data class UpdateUserResponse(
        val request_id: String,
        val user_id: String,
        val emails: StytchDataTypes.Emails?,
        val phone_numbers: StytchDataTypes.PhoneNumbers?,
    )

    @JsonClass(generateAdapter = true)
    public data class DeleteUserResponse(
        val request_id: String,
        val user_id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class DeleteUserEmailResponse(
        val request_id: String,
        val user_id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class DeleteUserPhoneNumberResponse(
        val request_id: String,
        val user_id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class GetPendingUsersResponse(
        val request_id: String,
        val users: List<StytchDataTypes.User>,
        val has_more: Boolean,
        val next_starting_after_id: String,
        val total: Int,
    )

    @JsonClass(generateAdapter = true)
    public data class SendMagicLinkResponse(
        val request_id: String,
        val user_id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class SendMagicLinkByEmailResponse(
        val request_id: String,
        val user_id: String,
        val email_id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class LoginOrCreateUserByEmailResponse(
        val request_id: String,
        val user_id: String,
        val email_id: String,
        val user_created: Boolean,
    )

    @JsonClass(generateAdapter = true)
    public data class InviteByEmailResponse(
        val request_id: String,
        val user_id: String,
        val email_id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class AuthenticateUserResponse(
        val request_id: String,
        val user_id: String,
        val method_id: String,
    ) : Serializable

    @JsonClass(generateAdapter = true)
    public data class RevokeAPendingInviteResponse(
        val request_id: String,
    )

    @JsonClass(generateAdapter = true)
    public data class SendOneTimePasscodeBySMSResponse(
        val request_id: String,
        val user_id: String,
        val phone_id: String,
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
    val error_method: String?,
    val error_url: String,
)

public object StytchErrorTypes {
    public const val EMAIL_NOT_FOUND: String = "email_not_found"
    public const val BILLING_NOT_VERIFIED_FOR_EMAIL: String = "billing_not_verified_for_email"
    public const val UNABLE_TO_AUTH_MAGIC_LINK: String = "unable_to_auth_magic_link"
    public const val INVALID_USER_ID: String = "invalid_user_id"
    public const val UNAUTHORIZED_CREDENTIALS: String = "unauthorized_credentials"
}

internal interface StytchApiService {
    @POST("users")
    suspend fun createUser(
        @Body request: StytchRequestTypes.CreateUserRequest,
    ): StytchResponseTypes.CreateUserResponse

    @GET("users/{user_id}")
    suspend fun getUser(
        @Path("user_id") userId: String,
    ): StytchResponseTypes.GetUserResponse

    @PUT("users/{user_id")
    suspend fun updateUser(
        @Path("user_id") userId: String,
        @Body request: StytchRequestTypes.UpdateUserRequest,
    ): StytchResponseTypes.UpdateUserResponse

    @DELETE("users/{user_id}")
    suspend fun deleteUser(
        @Path("user_id") userId: String,
    ): StytchResponseTypes.DeleteUserResponse

    @DELETE("users/emails/{user_id}")
    suspend fun deleteUserEmail(
        @Path("user_id") userId: String,
    ): StytchResponseTypes.DeleteUserEmailResponse

    @DELETE("users/phone_numbers/{user_id}")
    suspend fun deleteUserPhoneNumber(
        @Path("user_id") userId: String,
    ): StytchResponseTypes.DeleteUserPhoneNumberResponse

    @GET("users/pending")
    suspend fun getPendingUsers(
        @Body request: StytchRequestTypes.GetPendingUsersRequest,
    ): StytchResponseTypes.GetPendingUsersResponse

    @POST("magic_links/send")
    suspend fun sendMagicLink(
        @Body request: StytchRequestTypes.SendMagicLinkRequest,
    ): StytchResponseTypes.SendMagicLinkResponse

    @POST("magic_links/send_by_email")
    suspend fun sendMagicLinkByEmail(
        @Body request: StytchRequestTypes.SendMagicLinkByEmailRequest,
    ): StytchResponseTypes.SendMagicLinkByEmailResponse

    @POST("magic_links/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequestTypes.LoginOrCreateUserByEmailRequest,
    ): StytchResponseTypes.LoginOrCreateUserByEmailResponse

    @POST("magic_links/invite_by_email")
    suspend fun inviteByEmail(
        @Body request: StytchRequestTypes.InviteByEmailRequest,
    ): StytchResponseTypes.InviteByEmailResponse

    @POST("magic_links/{token}/authenticate")
    suspend fun authenticateMagicLink(
        @Path("token") token: String,
        @Body request: StytchRequestTypes.AuthenticateMagicLinkRequest,
    ): StytchResponseTypes.AuthenticateUserResponse

    @POST("magic_links/revoke_invite")
    suspend fun revokeAPendingInvite(
        @Body request: StytchRequestTypes.RevokeAPendingInviteRequest,
    ): StytchResponseTypes.RevokeAPendingInviteResponse

    @POST("otp/send_by_sms")
    suspend fun sendOneTimePasscodeBySMS(
        @Body request: StytchRequestTypes.SendOneTimePasscodeBySMSRequest,
    ): StytchResponseTypes.SendOneTimePasscodeBySMSResponse

    @POST("otp/login_or_create")
    suspend fun loginOrCreateUserBySMS(
        @Body request: StytchRequestTypes.LoginOrCreateUserBySMSRequest,
    ): StytchResponseTypes.LoginOrCreateUserBySMSResponse

    @POST("otp/authenticate")
    suspend fun authenticateOneTimePasscode(
        @Body request: StytchRequestTypes.AuthenticateOneTimePasscodeRequest,
    ): StytchResponseTypes.AuthenticateUserResponse
}

public sealed class StytchResult<out T> {
    public data class Success<out T>(val value: T) : StytchResult<T>()
    public object NetworkError : StytchResult<Nothing>()
    public data class Error(val errorCode: Int, val errorResponse: StytchErrorResponse?) : StytchResult<Nothing>()
}

internal val moshi by lazy { Moshi.Builder().build() }
internal val moshiErrorAdapter by lazy { moshi.adapter(StytchErrorResponse::class.java) }

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
                        moshiErrorAdapter.fromJson(it)
                    }
                } catch (t: Throwable) {
                    null
                }
                warningLog("http error code: $errorCode, errorResponse: $stytchErrorResponse")
                StytchResult.Error(errorCode = errorCode, errorResponse = stytchErrorResponse)
            }
            else -> {
                warningLog("Network Error")
                StytchResult.NetworkError
            }
        }
    }
}

internal fun warningLog(message: String) {
    Log.w("StytchLog", "Stytch warning: $message")
}

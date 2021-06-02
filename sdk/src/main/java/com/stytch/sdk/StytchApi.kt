package com.stytch.sdk

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

public object StytchApi {

    public fun asyncUtil(block: suspend () -> Unit) {
        GlobalScope.launch { block() }
    }

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
            startingAfterId: String? = null
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

    private val apiService by lazy {
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

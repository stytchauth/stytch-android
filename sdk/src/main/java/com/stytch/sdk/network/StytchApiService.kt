package com.stytch.sdk.network

import com.stytch.sdk.StytchClient
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val ONE_HUNDRED_TWENTY = 120L
private const val HTTP_UNAUTHORIZED = 401

@Suppress("TooManyFunctions")
internal interface StytchApiService {

    //region Magic Links
    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.MagicLinks.Email.LoginOrCreateUserRequest
    ): StytchResponses.MagicLinks.Email.LoginOrCreateUserResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.MagicLinks.AuthenticateRequest
    ): StytchResponses.AuthenticateResponse
    //endregion Magic Links

    //region Sessions
    @POST("sessions/authenticate")
    suspend fun authenticateSessions(
        @Body request: StytchRequests.Sessions.AuthenticateRequest
    ): StytchResponses.AuthenticateResponse

    @POST("sessions/revoke")
    suspend fun revokeSessions(): StytchResponses.Sessions.RevokeResponse
    //endregion Sessions

    //region OTP
    @POST("otps/sms/login_or_create")
    suspend fun loginOrCreateUserByOTPWithSMS(
        @Body request: StytchRequests.OTP.SMS
    ): StytchResponses.LoginOrCreateOTPResponse

    @POST("otps/whatsapp/login_or_create")
    suspend fun loginOrCreateUserByOTPWithWhatsApp(
        @Body request: StytchRequests.OTP.WhatsApp
    ): StytchResponses.LoginOrCreateOTPResponse

    @POST("otps/email/login_or_create")
    suspend fun loginOrCreateUserByOTPWithEmail(
        @Body request: StytchRequests.OTP.Email
    ): StytchResponses.LoginOrCreateOTPResponse

    @POST("otps/authenticate") // TODO Need to create a proper name to differentiate fom magiclinks authenticate
    suspend fun authenticateWithOTP(
        @Body request: StytchRequests.OTP.Authenticate
    ): StytchResponses.AuthenticateResponse
    //endregionOTP

    //region passwords
    @POST("passwords")
    suspend fun passwords(
        @Body request: StytchRequests.Passwords.CreateRequest
    ): StytchResponses.Passwords.PasswordsCreateResponse

    @POST("passwords/authenticate")
    suspend fun authenticateWithPasswords(
        @Body request: StytchRequests.Passwords.AuthenticateRequest
    ): StytchResponses.AuthenticateResponse

    @POST("passwords/email/reset/start")
    suspend fun resetByEmailStart(
        @Body request: StytchRequests.Passwords.ResetByEmailStartRequest
    ): StytchResponses.BasicResponse

    @POST("passwords/email/reset")
    suspend fun resetByEmail(
        @Body request: StytchRequests.Passwords.ResetByEmailRequest,
    ): StytchResponses.AuthenticateResponse

    @POST("passwords/strength_check")
    suspend fun strengthCheck(
        @Body request: StytchRequests.Passwords.StrengthCheckRequest
    ): StytchResponses.Passwords.PasswordsStrengthCheckResponse
    //endregion passwords

    companion object {
        private fun clientBuilder(authHeaderInterceptor: StytchAuthHeaderInterceptor?): OkHttpClient {
            val builder = OkHttpClient.Builder()
                .readTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
                .writeTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
                .connectTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
            authHeaderInterceptor?.let { builder.addInterceptor(it) }
            builder
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
            return builder.build()
        }
        fun createApiService(hostUrl: String, authHeaderInterceptor: StytchAuthHeaderInterceptor?): StytchApiService {
            return Retrofit.Builder()
                .baseUrl(hostUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(clientBuilder(authHeaderInterceptor))
                .build()
                .create(StytchApiService::class.java)
        }
    }
}

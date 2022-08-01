package com.stytch.sdk.network

import com.stytch.sdk.StytchRequestTypes
import com.stytch.sdk.StytchResponseTypes
import retrofit2.http.Body
import retrofit2.http.POST

internal interface StytchApiService {
    @Deprecated("", ReplaceWith("loginOrCreateUserByEmail", "StytchRequests.LoginOrCreateUserByEmailRequest"))
    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequestTypes.SDKLoginOrCreateUserByEmailRequest,
    ): StytchResponseTypes.LoginOrCreateUserByEmailResponse

    @Deprecated("")
    @POST("sdk/otps/sms/login_or_create")
    suspend fun loginOrCreateUserBySMS(
        @Body request: StytchRequestTypes.SDKLoginOrCreateUserBySMSRequest,
    ): StytchResponseTypes.LoginOrCreateUserBySMSResponse

    @POST("magic_links/email/login_or_create")
    suspend fun loginOrCreateUserByEmail(
        @Body request: StytchRequests.LoginOrCreateUserByEmailRequest,
    ): StytchResponses.LoginOrCreateUserByEmailResponse

    @POST("magic_links/authenticate")
    suspend fun authenticate(
        @Body request: StytchRequests.Authenticate
    ): StytchResponses.BasicResponse




}
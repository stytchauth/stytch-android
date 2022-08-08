package com.stytch.exampleapp

import com.stytch.sdk.BaseResponse
import com.stytch.sdk.LoginOrCreateUserByEmailResponse
import com.stytch.sdk.StytchClient

interface SignInRepository {

    suspend fun authenticate(token: String, expirationTime: Int): BaseResponse
    suspend fun loginOrCreate(params: StytchClient.MagicLinks.Parameters): LoginOrCreateUserByEmailResponse

}

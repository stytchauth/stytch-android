package com.stytch.sdk.network

import com.stytch.sdk.StytchClient
import okhttp3.Interceptor
import okhttp3.Response

internal class UnauthorizedResponseInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 401) {
            StytchClient.sessionStorage.revoke()
        }
        return response
    }
}


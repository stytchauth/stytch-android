package com.stytch.sdk.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authorizationValue: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", authorizationValue)
            .build()
        return chain.proceed(request)

    }
}
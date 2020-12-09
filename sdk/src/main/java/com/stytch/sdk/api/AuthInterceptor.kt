package com.stytch.sdk.api

import android.util.Base64
import com.stytch.sdk.Stytch
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(): Interceptor {

    private fun generateAuth(): String {
        return "Basic ${Base64.encodeToString("${Stytch.instance.config?.projectID}:${Stytch.instance.config?.secret}".toByteArray(), Base64.NO_WRAP)}"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", generateAuth())
            .build()
        return chain.proceed(request)

    }
}
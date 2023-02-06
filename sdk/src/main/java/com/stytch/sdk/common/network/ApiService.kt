package com.stytch.sdk.common.network

import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val ONE_HUNDRED_TWENTY = 120L
private const val HTTP_UNAUTHORIZED = 401

internal interface ApiService {
    companion object {
        private fun clientBuilder(
            authHeaderInterceptor: StytchAuthHeaderInterceptor?,
            revokeSession: () -> Unit,
        ): OkHttpClient {
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
                            revokeSession()
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

        fun <T : ApiService> createApiService(
            hostUrl: String,
            authHeaderInterceptor: StytchAuthHeaderInterceptor?,
            revokeSession: () -> Unit,
            apiService: Class<T>
        ): T {
            return Retrofit.Builder()
                .baseUrl(hostUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(clientBuilder(authHeaderInterceptor, revokeSession))
                .build()
                .create(apiService)
        }
    }
}

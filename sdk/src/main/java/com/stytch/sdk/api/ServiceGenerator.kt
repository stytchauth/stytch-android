package com.stytch.sdk.api

import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchEnvironment
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object ServiceGenerator {
    var apiBaseUrl = "https://api.stytch.com/v1/"
    var testApiBaseUrl = "https://test.stytch.com/v1/"

    fun createService(): StytchService {
        val httpClient = OkHttpClient.Builder()
        httpClient
            .readTimeout(120L, TimeUnit.SECONDS)
            .writeTimeout(120L, TimeUnit.SECONDS)
            .connectTimeout(120L, TimeUnit.SECONDS)

        val authInterceptor = AuthInterceptor()
        httpClient.addInterceptor(authInterceptor)

        val client = httpClient.build()
        return Retrofit.Builder()
            .baseUrl(
                when(Stytch.instance.environment){
                    StytchEnvironment.LIVE -> apiBaseUrl
                    StytchEnvironment.TEST -> testApiBaseUrl
                }
            )
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StytchService::class.java)
    }
}

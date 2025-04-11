package com.stytch.sdk.common.network

import com.squareup.moshi.Moshi
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.common.utils.createEnumJsonAdapter
import com.stytch.sdk.consumer.network.models.CryptoWalletType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val ONE_HUNDRED_TWENTY = 120L

internal open class ApiService(
    hostUrl: String,
) {
    private var client: OkHttpClient =
        OkHttpClient
            .Builder()
            .readTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
            .writeTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
            .connectTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
            .addNetworkInterceptor {
                // OkHttp is adding a charset to the content-type which is rejected by the API
                // see: https://github.com/square/okhttp/issues/3081
                it.proceed(
                    it
                        .request()
                        .newBuilder()
                        .header("Content-Type", "application/json")
                        .build(),
                )
            }.build()

    private val moshi =
        Moshi
            .Builder()
            .add(createEnumJsonAdapter<AllowedAuthMethods>())
            .add(createEnumJsonAdapter<MfaMethod>())
            .add(createEnumJsonAdapter<SetMFAEnrollment>())
            .add(createEnumJsonAdapter<CryptoWalletType>())
            .add(createEnumJsonAdapter<Locale>())
            .build()

    internal var retrofit =
        Retrofit
            .Builder()
            .baseUrl(hostUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

    fun addAuthHeaderInterceptor(authHeaderInterceptor: StytchAuthHeaderInterceptor?): Retrofit {
        val newBuilder = client.newBuilder()
        authHeaderInterceptor?.let {
            newBuilder.addInterceptor(it)
        }
        client = newBuilder.build()
        return retrofit.newBuilder().client(client).build()
    }

    fun addDfpInterceptor(dfpInterceptor: StytchDFPInterceptor): Retrofit {
        val newClientBuilder = client.newBuilder()
        newClientBuilder
            .interceptors()
            .find {
                it::class.java == StytchDFPInterceptor::class.java
            }?.let { existingInterceptor ->
                newClientBuilder.interceptors().remove(existingInterceptor)
            }
        client = newClientBuilder.addInterceptor(dfpInterceptor).build()
        return retrofit.newBuilder().client(client).build()
    }

    interface ApiEndpoints
}

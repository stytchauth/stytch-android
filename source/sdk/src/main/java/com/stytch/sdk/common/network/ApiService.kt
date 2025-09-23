package com.stytch.sdk.common.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.b2b.network.models.SetMFAEnrollment
import com.stytch.sdk.common.network.models.Locale
import com.stytch.sdk.common.utils.createEnumJsonAdapter
import com.stytch.sdk.consumer.network.models.CryptoWalletType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import java.util.concurrent.TimeUnit

private const val ONE_HUNDRED_TWENTY = 120L

internal open class ApiService(
    hostUrl: String,
    interceptors: List<Interceptor> = emptyList(),
) {
    private val client: OkHttpClient =
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
            }.apply {
                interceptors.forEach { interceptor ->
                    addInterceptor(interceptor)
                }
            }.build()

    private val moshi =
        Moshi
            .Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(createEnumJsonAdapter<AllowedAuthMethods>())
            .add(createEnumJsonAdapter<MfaMethod>())
            .add(createEnumJsonAdapter<SetMFAEnrollment>())
            .add(createEnumJsonAdapter<CryptoWalletType>())
            .add(createEnumJsonAdapter<Locale>())
            .build()

    internal val retrofit =
        Retrofit
            .Builder()
            .baseUrl(hostUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

    interface ApiEndpoints
}

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

internal interface ApiService {
    companion object {
        private fun clientBuilder(
            authHeaderInterceptor: StytchAuthHeaderInterceptor?,
            dfpInterceptor: StytchDFPInterceptor?,
        ): OkHttpClient {
            val builder =
                OkHttpClient
                    .Builder()
                    .readTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
                    .writeTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
                    .connectTimeout(ONE_HUNDRED_TWENTY, TimeUnit.SECONDS)
            authHeaderInterceptor?.let { builder.addInterceptor(it) }
            dfpInterceptor?.let { builder.addInterceptor(it) }
            builder
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
                }
            return builder.build()
        }

        fun <T : ApiService> createApiService(
            hostUrl: String,
            authHeaderInterceptor: StytchAuthHeaderInterceptor?,
            dfpInterceptor: StytchDFPInterceptor?,
            apiService: Class<T>,
        ): T {
            val moshi =
                Moshi
                    .Builder()
                    .add(createEnumJsonAdapter<AllowedAuthMethods>())
                    .add(createEnumJsonAdapter<MfaMethod>())
                    .add(createEnumJsonAdapter<SetMFAEnrollment>())
                    .add(createEnumJsonAdapter<CryptoWalletType>())
                    .add(createEnumJsonAdapter<Locale>())
                    .build()
            return Retrofit
                .Builder()
                .baseUrl(hostUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(clientBuilder(authHeaderInterceptor, dfpInterceptor))
                .build()
                .create(apiService)
        }
    }
}

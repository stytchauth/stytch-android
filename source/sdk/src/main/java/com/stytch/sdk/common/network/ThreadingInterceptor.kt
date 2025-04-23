package com.stytch.sdk.common.network

import android.net.TrafficStats
import okhttp3.Interceptor
import okhttp3.Response

internal class ThreadingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        TrafficStats.setThreadStatsTag(Thread.currentThread().id.toInt())
        return chain.proceed(chain.request())
    }
}

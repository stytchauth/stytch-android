package com.stytch.sdk.api

import android.util.Base64
import com.stytch.sdk.BuildConfig
import com.stytch.sdk.api.requests.CreateUserRequest
import com.stytch.sdk.api.requests.SendMagicLingRequest
import com.stytch.sdk.api.requests.VerifyTokenRequest
import com.stytch.sdk.api.responses.CreateUserResponse
import com.stytch.sdk.api.responses.SendMagicLingResponse
import com.stytch.sdk.api.responses.VerifyTokenResponse
import com.stytch.sdk.exceptions.ExceptionRecognizer
import com.stytch.sdk.exceptions.UnknownException
import com.stytch.sdk.helpers.LoggerLocal
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import java.util.concurrent.TimeUnit


const val BASE_URL = "https://test.stytch.com/v1/"


private const val TAG = "Api"
private const val API_KEY = "----"

class Api {

    private val service: StytchService
    private val accessToken: String = ""

    init {
        val httpClient = OkHttpClient.Builder()
        httpClient
            .readTimeout(120L, TimeUnit.SECONDS)
            .writeTimeout(120L, TimeUnit.SECONDS)
            .connectTimeout(120L, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) { // Adds logging to the OkHttp client
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(interceptor)
        }

        val authInterceptor = AuthInterceptor(generateAuth())
        httpClient.addInterceptor(authInterceptor)


        val client = httpClient.build()
        service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StytchService::class.java)
    }


    private fun generateAuth(): String {
        val secret = "secret-test-6-ma0PNENqjBVX6Dx2aPUIdhLFObauXx07c="
        val projectId = "project-test-d0dbafe6-a019-47ea-8550-d021c1c76ea9"

        return "Basic ${Base64.encodeToString("$projectId:$secret".toByteArray(), Base64.NO_WRAP)}"
    }

    fun sendMagicLink(
        email: String,
        onSuccess: (SendMagicLingResponse) -> Unit,
        onError: ((Throwable) -> Unit)?
    ) {
        service.sendMagicLink(SendMagicLingRequest(email, "https://stytch", 60)).enqueue(
            generateCallback(onSuccess, onError)
        )
    }

    fun createUser(
        email: String,
        onSuccess: (CreateUserResponse) -> Unit,
        onError: ((Throwable) -> Unit)?
    ) {
        service.createUser(CreateUserRequest(email)).enqueue(
            generateCallback(onSuccess, onError)
        )
    }

    fun verifyToken(
        token: String,
        onSuccess: (VerifyTokenResponse) -> Unit,
        onError: ((Throwable) -> Unit)?
    ) {
        service.verifyToken(token, VerifyTokenRequest())
            .enqueue(generateCallback(onSuccess, onError))
    }

    private fun <T> checkResult(
        response: Response<T>,
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ) {
        try {
            ExceptionRecognizer.recognize(response)
            val resp = response.body() ?: throw UnknownException()
            onSuccess(resp)
        } catch (ex: Exception) {
            onError?.invoke(ex)
        }
    }

    private fun <T> generateCallback(
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null
    ): Callback<T> {
        return object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                ExceptionRecognizer.recognize(t, onError)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                checkResult(response, onSuccess, onError)
            }
        }
    }


    companion object {
        val instance = Api()

        fun getIPAddress(useIPv4: Boolean): String? {
            try {
                val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress
                            LoggerLocal.d(TAG, "address: $sAddr")
                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4) return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                    return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(
                                        0,
                                        delim
                                    ).toUpperCase()
                                }
                            }
                        }
                    }
                }
            } catch (ignored: java.lang.Exception) {
            } // for now eat exceptions
            return ""
        }
    }

}
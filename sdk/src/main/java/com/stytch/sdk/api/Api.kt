package com.stytch.sdk.api

import android.util.Base64
import com.stytch.sdk.BuildConfig
import com.stytch.sdk.Stytch
import com.stytch.sdk.api.requests.*
import com.stytch.sdk.api.responses.CreateUserResponse
import com.stytch.sdk.api.responses.SendEmailVerificationResponse
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

        val authInterceptor = AuthInterceptor()
        httpClient.addInterceptor(authInterceptor)


        val client = httpClient.build()
        service = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StytchService::class.java)
    }

    fun sendMagicLink(
        email: String
    ): Response<SendMagicLingResponse> {
        return service.sendMagicLink(SendMagicLingRequest(email, "https://stytch", 60)).execute()
    }

    fun createUser(
        email: String
    ): Response<CreateUserResponse> {
        return service.createUser(CreateUserRequest(email)).execute()
    }

    fun verifyToken(
        token: String
    ): Response<VerifyTokenResponse> {
        return service.verifyToken(token, VerifyTokenRequest()).execute()
    }

    fun sendEmailVerification(emailId: String, userId: String): Response<SendEmailVerificationResponse> {
        return service.sendEmailVerification(emailId, SendEmailVerificationRequest()).execute()
    }

    fun deleteUser(userId: String){
        service.deleteUser(userId, DeleteUserRequest()).execute()
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
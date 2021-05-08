package com.stytch.sdk.api

import android.util.Base64
import com.stytch.sdk.Constants
import com.stytch.sdk.ExceptionRecognizer
import com.stytch.sdk.LoggerLocal
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchEnvironment
import com.stytch.sdk.UnknownException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.concurrent.TimeUnit

internal class StytchApi {

    private val service: StytchApiService get() = ServiceGenerator.createService()

    fun sendMagicLink(
        email: String,
    ): Response<SendMagicLingResponse> {
        return service.sendMagicLink(
            SendMagicLinkRequest(
                email,
                "${Stytch.instance.config?.deepLinkScheme}://${Stytch.instance.config?.deepLinkHost}/magic_link",
                60
            )
        ).execute()
    }

    fun loginOrSignUp(
        email: String,
        createUserAsPending: Boolean,
    ): Response<SendMagicLingResponse> {
        return service.loginOrSignUp(
            LoginOrSignUpRequest(
                email,
                Constants.LOGIN_PATH.deepLink(),
                Constants.SIGN_UP_PATH.deepLink(),
                Constants.LOGIN_EXPIRATION,
                Constants.SIGNUP_EXPIRATION,
                createUserAsPending,
            )
        ).execute()
    }

    fun createUser(
        email: String,
    ): Response<CreateUserResponse> {
        return service.createUser(CreateUserRequest(email)).execute()
    }

    fun verifyToken(
        token: String,
    ): Response<VerifyTokenResponse> {
        return service.verifyToken(token, VerifyTokenRequest()).execute()
    }

    fun sendEmailVerification(
        emailId: String,
        userId: String,
    ): Response<SendEmailVerificationResponse> {
        return service.sendEmailVerification(emailId, SendEmailVerificationRequest()).execute()
    }

    fun deleteUser(userId: String) {
        service.deleteUser(userId, DeleteUserRequest()).execute()
    }

    private fun <T> checkResult(
        response: Response<T>,
        onSuccess: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null,
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
        onError: ((Throwable) -> Unit)? = null,
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
        private const val TAG = "Api"

        var instance = StytchApi()

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

internal class AuthInterceptor : Interceptor {

    private fun generateAuth(): String {
        val encoding = Base64.encodeToString(
            "${Stytch.instance.config?.projectID}:${Stytch.instance.config?.secret}".toByteArray(),
            Base64.NO_WRAP,
        )
        return "Basic $encoding"
    }

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", generateAuth())
            .build()
        return chain.proceed(request)
    }
}

internal object ServiceGenerator {
    var apiBaseUrl = "https://api.stytch.com/v1/"
    var testApiBaseUrl = "https://test.stytch.com/v1/"

    fun createService(): StytchApiService {
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
                when (Stytch.instance.environment) {
                    StytchEnvironment.LIVE -> apiBaseUrl
                    StytchEnvironment.TEST -> testApiBaseUrl
                }
            )
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(StytchApiService::class.java)
    }
}

internal fun String.deepLink(): String {
    return "${Stytch.instance.config?.deepLinkScheme}://${Stytch.instance.config?.deepLinkHost}/$this"
}

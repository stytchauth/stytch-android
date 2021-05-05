package com.stytch.sdk

import android.util.Base64
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.concurrent.TimeUnit

internal interface StytchService {

    @POST("magic_links/send_by_email")
    fun sendMagicLink(
        @Body request: SendMagicLinkRequest,
    ): Call<SendMagicLingResponse>

    @POST("magic_links/login_or_invite")
    fun loginOrInvite(
        @Body request: LoginOrInviteRequest,
    ): Call<SendMagicLingResponse>

    @POST("magic_links/login_or_create")
    fun loginOrSignUp(
        @Body request: LoginOrSignUpRequest,
    ): Call<SendMagicLingResponse>

    @POST("users")
    fun createUser(
        @Body request: CreateUserRequest,
    ): Call<CreateUserResponse>

    @POST("magic_links/{token}/authenticate")
    fun verifyToken(
        @Path("token") token: String,
        @Body request: VerifyTokenRequest,
    ): Call<VerifyTokenResponse>

    @POST("emails/{email_id}/send_verification")
    fun sendEmailVerification(
        @Path("email_id") emailId: String,
        @Body request: SendEmailVerificationRequest,
    ): Call<SendEmailVerificationResponse>


    @POST("users/{user_id}")
    fun deleteUser(
        @Path("user_id") userId: String,
        @Body request: DeleteUserRequest,
    ): Call<DeleteUserResponse>
}

internal class Api {

    private val service: StytchService get() = ServiceGenerator.createService()

    fun sendMagicLink(
        email: String
    ): Response<SendMagicLingResponse> {
        return service.sendMagicLink(
            SendMagicLinkRequest(
                email,
                "${Stytch.instance.config?.deepLinkScheme}://${Stytch.instance.config?.deepLinkHost}/magic_link",
                60
            )
        ).execute()
    }

    fun loginOrInvite(
        email: String
    ): Response<SendMagicLingResponse> {
        return service.loginOrInvite(
            LoginOrInviteRequest(
                email,
                Constants.LOGIN_PATH.deepLink(),
                Constants.INVITE_PATH.deepLink(),
                Constants.LOGIN_EXPIRATION,
                Constants.INVITE_EXPIRATION
            )
        ).execute()
    }

    fun loginOrSignUp(
        email: String
    ): Response<SendMagicLingResponse> {
        return service.loginOrSignUp(
            LoginOrSignUpRequest(
                email,
                Constants.LOGIN_PATH.deepLink(),
                Constants.SIGN_UP_PATH.deepLink(),
                Constants.LOGIN_EXPIRATION,
                Constants.SIGNUP_EXPIRATION
            )
        ).execute()
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

    fun sendEmailVerification(
        emailId: String,
        userId: String
    ): Response<SendEmailVerificationResponse> {
        return service.sendEmailVerification(emailId, SendEmailVerificationRequest()).execute()
    }

    fun deleteUser(userId: String) {
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
        private const val TAG = "Api"

        var instance = Api()

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

internal class AuthInterceptor: Interceptor {

    private fun generateAuth(): String {
        return "Basic ${Base64.encodeToString("${Stytch.instance.config?.projectID}:${Stytch.instance.config?.secret}".toByteArray(), Base64.NO_WRAP)}"
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

public data class StytchResult(
    public val userId: String,
    public val requestId: String,
){
    override fun toString(): String {
        return "StytchResult(userId='$userId', requestId='$requestId')"
    }
}

public class BasicErrorResponse(
    @SerializedName("status") public val status: Int,
    @SerializedName("message") public val message: String?,
    @SerializedName("error_type") public val error_type: String?,
    @SerializedName("error_message") public val error_message: String?,
    @SerializedName("error_url") public val error_url: String?,
) {
}

public open class BasicResponse {
}

public class CreateUserResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
    @SerializedName("email_id") public val email_id: String,
) : BasicResponse() {
}

public class DeleteUserResponse: BasicResponse() {
}

public class SendEmailVerificationResponse(
    @SerializedName("user_id") public val user_id: String,
) : BasicResponse() {
}

public class SendMagicLingResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
    @SerializedName("user_created") public val user_created: Boolean,
    @SerializedName("email_id") public val email_id: String,
) : BasicResponse() {
}

public class VerifyTokenResponse(
    @SerializedName("request_id") public val request_id: String,
    @SerializedName("user_id") public val user_id: String,
) : BasicResponse() {
}

public open class BasicRequest {

    @SerializedName("attributes")
    public val attributes: Attributes = Attributes(
        Api.getIPAddress(true),
        System.getProperty("http.agent"),
    )

    public class Attributes(
        @SerializedName("ip_address") public val ip_address: String?,
        @SerializedName("user_agent") public val user_agent: String?,
    )
}

public class CreateUserRequest(
    @SerializedName("email") public val email: String,
): BasicRequest() {
}

public class DeleteUserRequest(): BasicRequest() {
}

public class LoginOrInviteRequest(
    @SerializedName("email") public val email: String,
    @SerializedName("login_magic_link_url") public val login_magic_link_url: String,
    @SerializedName("invite_magic_link_url") public val invite_magic_link_url: String,
    @SerializedName("login_expiration_minutes") public val login_expiration_minutes: Long,
    @SerializedName("invite_expiration_minutes") public val invite_expiration_minutes: Long,
) : BasicRequest() {
}

public class LoginOrSignUpRequest(
    @SerializedName("email") public val email: String,
    @SerializedName("login_magic_link_url") public val login_magic_link_url: String,
    @SerializedName("signup_magic_link_url") public val signup_magic_link_url: String,
    @SerializedName("login_expiration_minutes") public val login_expiration_minutes: Long,
    @SerializedName("signup_expiration_minutes") public val signup_expiration_minutes: Long,
) : BasicRequest() {
}

public class SendEmailVerificationRequest(): BasicRequest() {
}

public class SendMagicLinkRequest(
    @SerializedName("email") public val email: String,
    @SerializedName("magic_link_url") public val magic_link_url: String,
    @SerializedName("expiration_minutes") public val expiration_minutes: Long,
) : BasicRequest() {
}

public class VerifyTokenRequest(
    @SerializedName("ip_match_required") public val ip_match_required: Boolean? = null,
    @SerializedName("user_agent_match_required") public val user_agent_match_required: Boolean? = null,
): BasicRequest() {
}

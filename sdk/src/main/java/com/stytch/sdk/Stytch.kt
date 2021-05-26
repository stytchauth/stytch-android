package com.stytch.sdk

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.wealthfront.magellan.Screen
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.Serializable
import java.util.concurrent.TimeUnit

public object Stytch {
    internal var isInitialized = false

    internal lateinit var authorizationHeader: String
    internal lateinit var environment: StytchEnvironment

    public fun configure(
        projectId: String,
        secret: String,
        environment: StytchEnvironment,
    ) {
        authorizationHeader = generateAuthorizationHeader(projectId = projectId, secret = secret)
        this.environment = environment
        isInitialized = true
    }
}

public object StytchApi {

    public fun asyncUtil(block: suspend () -> Unit) {
        GlobalScope.launch { block() }
    }

    public object Users {
        public suspend fun createUser(
            email: String? = null,
            phoneNumber: String? = null,
            name: StytchDataTypes.Name? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.CreateUserResponse> = safeApiCall {
            apiService.createUser(
                StytchRequestTypes.CreateUserRequest(
                    email = email,
                    phone_number = phoneNumber,
                    name = name,
                    attributes = attributes,
                )
            )
        }

        public suspend fun getUser(
            userId: String,
        ): StytchResult<StytchResponseTypes.GetUserResponse> = safeApiCall {
            apiService.getUser(
                userId = userId,
            )
        }

        public suspend fun updateUser(
            userId: String,
            name: StytchDataTypes.Name? = null,
            email: String? = null,
            phoneNumber: String? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.UpdateUserResponse> = safeApiCall {
            apiService.updateUser(
                userId = userId,
                request = StytchRequestTypes.UpdateUserRequest(
                    name = name,
                    emails = if (email == null) null else StytchDataTypes.Emails(email),
                    phone_numbers = if (phoneNumber == null) null else StytchDataTypes.PhoneNumbers(phoneNumber),
                    attributes = attributes,
                )
            )
        }

        public suspend fun deleteUser(
            userId: String,
        ): StytchResult<StytchResponseTypes.DeleteUserResponse> = safeApiCall {
            apiService.deleteUser(
                userId = userId,
            )
        }

        public suspend fun deleteUserEmail(
            userId: String,
        ): StytchResult<StytchResponseTypes.DeleteUserEmailResponse> = safeApiCall {
            apiService.deleteUserEmail(
                userId = userId,
            )
        }

        public suspend fun deleteUserPhoneNumber(
            userId: String,
        ): StytchResult<StytchResponseTypes.DeleteUserPhoneNumberResponse> = safeApiCall {
            apiService.deleteUserPhoneNumber(
                userId = userId,
            )
        }

        public suspend fun getPendingUsers(
            limit: Int? = null,
            startingAfterId: String? = null
        ): StytchResult<StytchResponseTypes.GetPendingUsersResponse> = safeApiCall {
            apiService.getPendingUsers(
                StytchRequestTypes.GetPendingUsersRequest(
                    limit = limit,
                    starting_after_id = startingAfterId,
                )
            )
        }
    }

    public object MagicLinks {
        public suspend fun sendMagicLink(
            userId: String,
            methodId: String,
            magicLinkUrl: String,
            expirationMinutes: Int? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.SendMagicLinkResponse> = safeApiCall {
            apiService.sendMagicLink(
                StytchRequestTypes.SendMagicLinkRequest(
                    user_id = userId,
                    method_id = methodId,
                    magic_link_url = magicLinkUrl,
                    expiration_minutes = expirationMinutes,
                    attributes = attributes,
                )
            )
        }

        public suspend fun sendMagicLinkByEmail(
            email: String,
            magicLinkUrl: String,
            expirationMinutes: Int? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.SendMagicLinkByEmailResponse> = safeApiCall {
            apiService.sendMagicLinkByEmail(
                StytchRequestTypes.SendMagicLinkByEmailRequest(
                    email = email,
                    magic_link_url = magicLinkUrl,
                    expiration_minutes = expirationMinutes,
                    attributes = attributes,
                )
            )
        }

        public suspend fun loginOrCreateUserByEmail(
            email: String,
            loginMagicLinkUrl: String,
            signupMagicLinkUrl: String,
            loginExpirationMinutes: Int? = null,
            signupExpirationMinutes: Int? = null,
            createUserAsPending: Boolean? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.LoginOrCreateUserByEmailResponse> = safeApiCall {
            apiService.loginOrCreateUserByEmail(
                StytchRequestTypes.LoginOrCreateUserByEmailRequest(
                    email = email,
                    login_magic_link_url = loginMagicLinkUrl,
                    signup_magic_link_url = signupMagicLinkUrl,
                    login_expiration_minutes = loginExpirationMinutes,
                    signup_expiration_minutes = signupExpirationMinutes,
                    create_user_as_pending = createUserAsPending,
                    attributes = attributes,
                )
            )
        }

        public suspend fun inviteByEmail(
            email: String,
            magicLinkUrl: String,
            expirationMinutes: Int? = null,
            name: StytchDataTypes.Name? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.InviteByEmailResponse> = safeApiCall {
            apiService.inviteByEmail(
                StytchRequestTypes.InviteByEmailRequest(
                    email = email,
                    magic_link_url = magicLinkUrl,
                    expiration_minutes = expirationMinutes,
                    name = name,
                    attributes = attributes,
                )
            )
        }

        public suspend fun authenticateMagicLink(
            token: String,
            options: StytchDataTypes.Options? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.AuthenticateUserResponse> = safeApiCall {
            apiService.authenticateMagicLink(
                token = token,
                StytchRequestTypes.AuthenticateMagicLinkRequest(
                    options = options,
                    attributes = attributes,
                )
            )
        }

        public suspend fun revokeAPendingInvite(
            email: String,
        ): StytchResult<StytchResponseTypes.RevokeAPendingInviteResponse> = safeApiCall {
            apiService.revokeAPendingInvite(
                StytchRequestTypes.RevokeAPendingInviteRequest(
                    email = email,
                )
            )
        }
    }

    public object OTP {
        public suspend fun sendOneTimePasscodeBySMS(
            phoneNumber: String,
            expirationMinutes: Int? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.SendOneTimePasscodeBySMSResponse> = safeApiCall {
            apiService.sendOneTimePasscodeBySMS(
                StytchRequestTypes.SendOneTimePasscodeBySMSRequest(
                    phone_number = phoneNumber,
                    expiration_minutes = expirationMinutes,
                    attributes = attributes,
                )
            )
        }

        public suspend fun loginOrCreateUserBySMS(
            phoneNumber: String,
            expirationMinutes: Int? = null,
            createUserAsPending: Boolean? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.LoginOrCreateUserBySMSResponse> = safeApiCall {
            apiService.loginOrCreateUserBySMS(
                StytchRequestTypes.LoginOrCreateUserBySMSRequest(
                    phone_number = phoneNumber,
                    expiration_minutes = expirationMinutes,
                    create_user_as_pending = createUserAsPending,
                    attributes = attributes,
                )
            )
        }

        public suspend fun authenticateOneTimePasscode(
            methodId: String,
            code: String,
            options: StytchDataTypes.Options? = null,
            attributes: StytchDataTypes.Attributes? = null,
        ): StytchResult<StytchResponseTypes.AuthenticateUserResponse> = safeApiCall {
            apiService.authenticateOneTimePasscode(
                StytchRequestTypes.AuthenticateOneTimePasscodeRequest(
                    method_id = methodId,
                    code = code,
                    options = options,
                    attributes = attributes,
                )
            )
        }
    }

    private val apiService by lazy {
        assertInitialized()
        Retrofit.Builder()
            .baseUrl(Stytch.environment.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .readTimeout(120L, TimeUnit.SECONDS)
                    .writeTimeout(120L, TimeUnit.SECONDS)
                    .connectTimeout(120L, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        chain.proceed(
                            chain.request()
                                .newBuilder()
                                .addHeader("Authorization", Stytch.authorizationHeader)
                                .addHeader("Content-Type", "application/json")
                                .build()
                        )
                    }
                    .build()
            )
            .build()
            .create(StytchApiService::class.java)
    }
}

public enum class StytchEnvironment(internal val baseUrl: String) {
    LIVE("https://api.stytch.com/v1/"),
    TEST("https://test.stytch.com/v1/"),
}

public object StytchUI {
    private var _uiCustomization: StytchUICustomization? = null
    public var uiCustomization: StytchUICustomization
        set(value) {
            _uiCustomization = value
        }
        get() {
            if (_uiCustomization == null) uiCustomization = StytchUICustomization()
            return _uiCustomization!!
        }

    public object EmailMagicLink {
        public data class Configuration(
            val loginMagicLinkUrl: String,
            val signupMagicLinkUrl: String,
            val createUserAsPending: Boolean,
        ): Serializable
    }

    public object SMSPasscode {
        public data class Configuration(
            val hashStringSet: Boolean,
            val createUserAsPending: Boolean,
        ): Serializable
    }
}

internal class StytchEmailMagicLinkActivity : StytchActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration = intent.getSerializableExtra<StytchUI.EmailMagicLink.Configuration>()
        configuration?.let { initNavigator(EmailMagicLinkHomeScreen(configuration)) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val token = intent?.data?.getQueryParameter("token")
        if (token != null) {
            // Verify deeplink
            GlobalScope.launch {
                val result = StytchApi.MagicLinks.authenticateMagicLink(token)
                when (result) {
                    is StytchResult.Success -> {
                        Log.d("StytchLog", "Successful Magic Link Authentication")
                        setResult(RESULT_OK, intentWithExtra(result.value))
                        finish()
                    }
                    is StytchResult.Error   -> {
                        Log.d("StytchLog", "Failed Magic Link Authentication")
                    }
                    StytchResult.NetworkError -> TODO()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentCodes.EMAIL_PICKER_INTENT_CODE.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                val emailAddress = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.id
                Log.d("StytchLog", "$emailAddress")
                emailAddress?.let {
                    (navigator.currentScreen() as? EmailMagicLinkHomeScreen)?.emailAddressHintGiven(it)
                }
            }
        }
    }
}

public class StytchSMSPasscodeActivity : StytchActivity() {
    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runCatching {
                if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
                    val extras = intent.extras
                    if ((extras?.get(SmsRetriever.EXTRA_STATUS) as? Status)?.statusCode == CommonStatusCodes.SUCCESS) {
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        startActivityForResult(consentIntent, IntentCodes.SMS_ONE_TAP_AUTOFILL_INTENT_CODE.ordinal)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configuration = intent.getSerializableExtra<StytchUI.SMSPasscode.Configuration>()
        configuration?.let { initNavigator(SMSPasscodeHomeScreen(configuration)) }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentCodes.PHONE_NUMBER_PICKER_INTENT_CODE.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                val phoneNumber = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.id
                phoneNumber?.let {
                    (navigator.currentScreen() as? SMSPasscodeHomeScreen)?.phoneNumberHintGiven(it)
                }
            }
            IntentCodes.SMS_ONE_TAP_AUTOFILL_INTENT_CODE.ordinal -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val smsMessage = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                smsMessage?.let {
                    (navigator.currentScreen() as? SMSPasscodeEnterPasscodeScreen)?.smsReceived(it)
                }
            }
        }
    }
}

internal enum class IntentCodes {
    EMAIL_PICKER_INTENT_CODE,
    PHONE_NUMBER_PICKER_INTENT_CODE,
    SMS_ONE_TAP_AUTOFILL_INTENT_CODE,
    SMS_ZERO_TAP_AUTOFILL_INTENT_CODE,
}

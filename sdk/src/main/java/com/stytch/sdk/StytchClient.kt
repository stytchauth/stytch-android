package com.stytch.sdk

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import com.stytch.sdk.biometrics.Biometrics
import com.stytch.sdk.biometrics.BiometricsImpl
import com.stytch.sdk.biometrics.BiometricsProviderImpl
import com.stytch.sdk.magicLinks.MagicLinks
import com.stytch.sdk.magicLinks.MagicLinksImpl
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.network.responseData.BasicData
import com.stytch.sdk.network.responseData.BiometricsAuthData
import com.stytch.sdk.network.responseData.CreateResponse
import com.stytch.sdk.network.responseData.DeleteAuthenticationFactorData
import com.stytch.sdk.network.responseData.IAuthData
import com.stytch.sdk.network.responseData.LoginOrCreateOTPData
import com.stytch.sdk.network.responseData.OAuthData
import com.stytch.sdk.network.responseData.StrengthCheckResponse
import com.stytch.sdk.network.responseData.UserData
import com.stytch.sdk.oauth.OAuth
import com.stytch.sdk.oauth.OAuthImpl
import com.stytch.sdk.otp.OTP
import com.stytch.sdk.otp.OTPImpl
import com.stytch.sdk.passwords.Passwords
import com.stytch.sdk.passwords.PasswordsImpl
import com.stytch.sdk.sessions.SessionStorage
import com.stytch.sdk.sessions.Sessions
import com.stytch.sdk.sessions.SessionsImpl
import com.stytch.sdk.userManagement.AuthenticationFactor
import com.stytch.sdk.userManagement.UserManagement
import com.stytch.sdk.userManagement.UserManagementImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Type alias for StytchResult<BasicData> used for loginOrCreateUserByEmail responses
 */
public typealias LoginOrCreateUserByEmailResponse = StytchResult<BasicData>

/**
 * Type alias for StytchResult<BasicData> used for basic responses
 */
public typealias BaseResponse = StytchResult<BasicData>

/**
 * Type alias for StytchResult<AuthData> used for authentication responses
 */
public typealias AuthResponse = StytchResult<IAuthData>

/**
 * Type alias for StytchResult<LoginOrCreateOTPData> used for loginOrCreateOTP responses
 */
public typealias LoginOrCreateOTPResponse = StytchResult<LoginOrCreateOTPData>

/**
 * Type alias for StytchResult<CreateResponse> used for PasswordsCreate responses
 */
public typealias PasswordsCreateResponse = StytchResult<CreateResponse>

/**
 * Type alias for StytchResult<StrengthCheckResponse> used for PasswordsStrengthCheck responses
 */
public typealias PasswordsStrengthCheckResponse = StytchResult<StrengthCheckResponse>

/**
 * Type alias for StytchResult<UserData> used for GetUser responses
 */
public typealias UserResponse = StytchResult<UserData>

/**
 * Type alias for StytchResult<DeleteAuthenticationFactorData> used for deleting authentication factors from a user
 */
public typealias DeleteFactorResponse = StytchResult<DeleteAuthenticationFactorData>

/**
 * Type alias for StytchResult<BiometricsAuthData> used for Biometrics authentication responses
 */
public typealias BiometricsAuthResponse = StytchResult<BiometricsAuthData>

/**
 * Type alias for StytchResult<OAuthData> used for Third Party OAuth authentication responses
 */
public typealias OAuthAuthenticatedResponse = StytchResult<OAuthData>

/**
 * The entrypoint for all Stytch-related interaction.
 */
public object StytchClient {
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal val sessionStorage = SessionStorage(StorageHelper)
    internal var externalScope: CoroutineScope = GlobalScope // TODO: SDK-614

    /**
     * Configures the StytchClient, setting the publicToken and hostUrl.
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @throws StytchExceptions.Critical - if failed to generate new encryption keys
     */
    public fun configure(context: Context, publicToken: String) {
        try {
            val deviceInfo = getDeviceInfo(context)
            StytchApi.configure(publicToken, deviceInfo)
            StorageHelper.initialize(context)
        } catch (ex: Exception) {
            throw StytchExceptions.Critical(ex)
        }
    }

    @Suppress("MaxLineLength")
    internal fun assertInitialized() {
        if (!StytchApi.isInitialized) {
            stytchError(
                "StytchClient not configured. You must call 'StytchClient.configure(...)' before using any functionality of the StytchClient." // ktlint-disable max-line-length
            )
        }
    }

    /**
     * Exposes an instance of email magic links
     */
    public var magicLinks: MagicLinks = MagicLinksImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchApi.MagicLinks.Email
    )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of otp
     */
    public var otps: OTP = OTPImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StytchApi.OTP
    )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of passwords
     */
    public var passwords: Passwords = PasswordsImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchApi.Passwords
    )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of sessions
     */
    public var sessions: Sessions = SessionsImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StytchApi.Sessions
    )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of Biometrics
     */
    public var biometrics: Biometrics = BiometricsImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchApi.Biometrics,
        BiometricsProviderImpl(),
    ) { biometricRegistrationId ->
        user.deleteFactor(AuthenticationFactor.BiometricRegistration(biometricRegistrationId))
    }
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of UserManagement
     */
    public var user: UserManagement = UserManagementImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StytchApi.UserManagement
    )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of OAuth
     */
    public var oauth: OAuth = OAuthImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchApi.OAuth
    )
        get() {
            assertInitialized()
            return field
        }
        internal set

    private fun getDeviceInfo(context: Context): DeviceInfo {
        val deviceInfo = DeviceInfo()
        deviceInfo.applicationPackageName = context.applicationContext.packageName
        deviceInfo.osVersion = Build.VERSION.SDK_INT.toString()
        deviceInfo.deviceName = Build.MODEL
        deviceInfo.osName = Build.VERSION.CODENAME

        try {
            // throw exceptions if packageName not found
            deviceInfo.applicationVersion = context
                .applicationContext
                .packageManager
                .getPackageInfo(deviceInfo.applicationPackageName!!, 0)
                .versionName
        } catch (ex: Exception) {
            deviceInfo.applicationVersion = ""
        }

        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels

        deviceInfo.screenSize = "($width,$height)"
        return deviceInfo
    }

    /**
     * Handle magic link
     * @param uri - intent.data from deep link
     * @param sessionDurationMinutes - sessionDuration
     * @return AuthResponse from backend after calling any of the authentication methods
     */
    public suspend fun handle(uri: Uri, sessionDurationMinutes: UInt): DeeplinkHandledStatus {
        assertInitialized()
        return withContext(dispatchers.io) {
            val token = uri.getQueryParameter(Constants.QUERY_TOKEN)
            if (token.isNullOrEmpty()) {
                return@withContext DeeplinkHandledStatus.NotHandled(StytchErrorType.DEEPLINK_MISSING_TOKEN.message)
            }
            when (TokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE))) {
                TokenType.MAGIC_LINKS -> {
                    DeeplinkHandledStatus.Handled(
                        magicLinks.authenticate(MagicLinks.AuthParameters(token, sessionDurationMinutes))
                    )
                }
                TokenType.OAUTH -> {
                    DeeplinkHandledStatus.Handled(
                        oauth.authenticate(OAuth.ThirdParty.AuthenticateParameters(token, sessionDurationMinutes))
                    )
                }
                TokenType.PASSWORD_RESET -> {
                    DeeplinkHandledStatus.ManualHandlingRequired(type = TokenType.PASSWORD_RESET, token = token)
                }
                TokenType.UNKNOWN -> {
                    DeeplinkHandledStatus.NotHandled(StytchErrorType.DEEPLINK_UNKNOWN_TOKEN.message)
                }
            }
        }
    }

    /**
     * Handle magic link
     * @param uri - intent.data from deep link
     * @param sessionDurationMinutes - sessionDuration
     * @param callback calls callback with AuthResponse response from backend
     */
    public fun handle(
        uri: Uri,
        sessionDurationMinutes: UInt,
        callback: (response: DeeplinkHandledStatus) -> Unit
    ) {
        externalScope.launch(dispatchers.ui) {
            val result = handle(uri, sessionDurationMinutes)
            // change to main thread to call callback
            callback(result)
        }
    }
}

internal object StytchLog {
    fun e(message: String) = Log.e("StytchLog", "Stytch error: $message")
    fun w(message: String) = Log.w("StytchLog", "Stytch warning: $message")
    fun i(message: String) = Log.i("StytchLog", message)
    fun d(message: String) = Log.d("StytchLog", message)
    fun v(message: String) = Log.v("StytchLog", message)
}

internal fun stytchError(message: String): Nothing {
    error("Stytch error: $message")
}

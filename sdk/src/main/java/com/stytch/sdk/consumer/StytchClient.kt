package com.stytch.sdk.consumer

import android.content.Context
import android.net.Uri
import android.os.Build
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.TokenType
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.common.stytchError
import com.stytch.sdk.consumer.biometrics.Biometrics
import com.stytch.sdk.consumer.biometrics.BiometricsImpl
import com.stytch.sdk.consumer.biometrics.BiometricsProviderImpl
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.magicLinks.MagicLinksImpl
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.oauth.OAuthImpl
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.consumer.otp.OTPImpl
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.consumer.passwords.PasswordsImpl
import com.stytch.sdk.consumer.sessions.SessionStorage
import com.stytch.sdk.consumer.sessions.Sessions
import com.stytch.sdk.consumer.sessions.SessionsImpl
import com.stytch.sdk.consumer.userManagement.UserAuthenticationFactor
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.consumer.userManagement.UserManagementImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        user.deleteFactor(UserAuthenticationFactor.BiometricRegistration(biometricRegistrationId))
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
     * @return DeeplinkHandledStatus from backend after calling any of the authentication methods
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
     * @param callback calls callback with DeeplinkHandledStatus response from backend
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

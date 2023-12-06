package com.stytch.sdk.consumer

import android.app.Application
import android.content.Context
import android.net.Uri
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.ActivityProvider
import com.stytch.sdk.common.dfp.CaptchaProviderImpl
import com.stytch.sdk.common.dfp.DFP
import com.stytch.sdk.common.dfp.DFPImpl
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.dfp.DFPProviderImpl
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.stytchError
import com.stytch.sdk.consumer.biometrics.Biometrics
import com.stytch.sdk.consumer.biometrics.BiometricsImpl
import com.stytch.sdk.consumer.biometrics.BiometricsProviderImpl
import com.stytch.sdk.consumer.extensions.launchSessionUpdater
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.consumer.magicLinks.MagicLinksImpl
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.oauth.OAuth
import com.stytch.sdk.consumer.oauth.OAuthImpl
import com.stytch.sdk.consumer.otp.OTP
import com.stytch.sdk.consumer.otp.OTPImpl
import com.stytch.sdk.consumer.passkeys.Passkeys
import com.stytch.sdk.consumer.passkeys.PasskeysImpl
import com.stytch.sdk.consumer.passwords.Passwords
import com.stytch.sdk.consumer.passwords.PasswordsImpl
import com.stytch.sdk.consumer.sessions.ConsumerSessionStorage
import com.stytch.sdk.consumer.sessions.Sessions
import com.stytch.sdk.consumer.sessions.SessionsImpl
import com.stytch.sdk.consumer.userManagement.UserAuthenticationFactor
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.consumer.userManagement.UserManagementImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The StytchClient object is your entrypoint to the Stytch Consumer SDK and is how you interact with all of our
 * supported authentication products.
 */
public object StytchClient {
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal val sessionStorage = ConsumerSessionStorage(StorageHelper)
    internal var externalScope: CoroutineScope = GlobalScope // TODO: SDK-614
    public var bootstrapData: BootstrapData = BootstrapData()
        internal set

    internal lateinit var dfpProvider: DFPProvider

    /**
     * Exposes a flow that reports the initialization state of the SDK. You can use this, or the optional callback in
     * the `configure()` method, to know when the Stytch SDK has been fully initialized and is ready for use
     */
    private var _isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    public val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param callback An optional callback that is triggered after configuration and initialization has completed
     * @throws StytchExceptions.Critical - if we failed to generate new encryption keys
     */
    public fun configure(context: Context, publicToken: String, callback: ((Boolean) -> Unit) = {}) {
        try {
            val deviceInfo = context.getDeviceInfo()
            StorageHelper.initialize(context)
            StytchApi.configure(publicToken, deviceInfo)
            val activityProvider = ActivityProvider(context.applicationContext as Application)
            dfpProvider = DFPProviderImpl(publicToken, activityProvider)
            maybeClearBadSessionToken()
            externalScope.launch(dispatchers.io) {
                bootstrapData = when (val res = StytchApi.getBootstrapData()) {
                    is StytchResult.Success -> res.value
                    else -> BootstrapData()
                }
                StytchApi.configureDFP(
                    dfpProvider = dfpProvider,
                    captchaProvider = CaptchaProviderImpl(
                        context.applicationContext as Application,
                        externalScope,
                        bootstrapData.captchaSettings.siteKey
                    ),
                    bootstrapData.dfpProtectedAuthEnabled,
                    bootstrapData.dfpProtectedAuthMode
                )
                // if there are session identifiers on device start the auto updater to ensure it is still valid
                if (sessionStorage.persistedSessionIdentifiersExist) {
                    StytchApi.Sessions.authenticate(null).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
                    }
                }
                _isInitialized.value = true
                callback(_isInitialized.value)
            }
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
     * Exposes an instance of the [MagicLinks] interface whicih provides methods for sending and authenticating users
     * with Email Magic Links.
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
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
     * Exposes an instance of the [OTP] interface which provides methods for sending and authenticating
     * One-Time Passcodes (OTP) via SMS, WhatsApp, and Email.
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
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
     * Exposes an instance of the [Passwords] interface which provides methods for authenticating, creating, resetting,
     * and performing strength checks of passwords.
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
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
     * Exposes an instance of the [Sessions] interface which provides methods for authenticating, updating, or revoking
     * sessions, and properties to retrieve the existing session token (opaque or JWT).
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
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
     * Exposes an instance of the [Biometrics] interface which provides methods for detecting biometric availability,
     * registering, authenticating, and removing biometrics identifiers.
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
     */
    public var biometrics: Biometrics = BiometricsImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchApi.Biometrics,
        BiometricsProviderImpl()
    ) { biometricRegistrationId ->
        user.deleteFactor(UserAuthenticationFactor.BiometricRegistration(biometricRegistrationId))
    }
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [UserManagement] interface which provides methods for retrieving an authenticated
     * user and deleting authentication factors from an authenticated user.
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
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
     * Exposes an instance of the [OAuth] interface which provides methods for authenticating a user via a native
     * Google OneTap prompt or any of our supported third-party OAuth providers
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
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

    /**
     * Exposes an instance of the [Passkeys] interface which provides methods for registering and authenticating
     * with Passkeys.
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
     */
    public var passkeys: Passkeys = PasskeysImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StytchApi.WebAuthn
    )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [DFP] interface which provides a method for retrieving a dfp_telemetry_id for use
     * in DFP lookups on your backend server
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchClient.configure()
     */
    public val dfp: DFP
        get() {
            assertInitialized()
            return DFPImpl(dfpProvider, dispatchers, externalScope)
        }

    /**
     * Call this method to parse out and authenticate deeplinks that your application receives. The currently supported
     * deeplink types are: Email Magic Links, Third-Party OAuth, and Password resets.
     *
     * For Email Magic Links and Third-Party OAuth deeplinks, it will return a [DeeplinkHandledStatus.Handled] class
     * containing either the authenticated response or error.
     *
     * For Password Reset deeplinks, it will return a [DeeplinkHandledStatus.ManualHandlingRequired] class containing
     * the relevant token, so that you can provide an appropriate UI to the user for resetting their password. The
     * returned token is used for making the subsequent StytchClient.passwords.resetByEmail() call.
     *
     * Any other link types passed to this method will return a [DeeplinkHandledStatus.NotHandled] class.
     * @param uri intent.data from deep link
     * @param sessionDurationMinutes desired session duration in minutes
     * @return [DeeplinkHandledStatus]
     */
    public suspend fun handle(uri: Uri, sessionDurationMinutes: UInt): DeeplinkHandledStatus {
        assertInitialized()
        return withContext(dispatchers.io) {
            val token = uri.getQueryParameter(Constants.QUERY_TOKEN)
            if (token.isNullOrEmpty()) {
                return@withContext DeeplinkHandledStatus.NotHandled(StytchErrorType.DEEPLINK_MISSING_TOKEN.message)
            }
            when (ConsumerTokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE))) {
                ConsumerTokenType.MAGIC_LINKS -> {
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            magicLinks.authenticate(MagicLinks.AuthParameters(token, sessionDurationMinutes))
                        )
                    )
                }
                ConsumerTokenType.OAUTH -> {
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            oauth.authenticate(OAuth.ThirdParty.AuthenticateParameters(token, sessionDurationMinutes))
                        )
                    )
                }
                ConsumerTokenType.PASSWORD_RESET -> {
                    DeeplinkHandledStatus.ManualHandlingRequired(type = ConsumerTokenType.PASSWORD_RESET, token = token)
                }
                else -> {
                    DeeplinkHandledStatus.NotHandled(StytchErrorType.DEEPLINK_UNKNOWN_TOKEN.message)
                }
            }
        }
    }

    /**
     * Call this method to parse out and authenticate deeplinks that your application receives. The currently supported
     * deeplink types are: Email Magic Links, Third-Party OAuth, and Password resets.
     *
     * For Email Magic Links and Third-Party OAuth deeplinks, it will return a [DeeplinkHandledStatus.Handled] class
     * containing either the authenticated response or error.
     *
     * For Password Reset deeplinks, it will return a [DeeplinkHandledStatus.ManualHandlingRequired] class containing
     * the relevant token, so that you can provide an appropriate UI to the user for resetting their password. The
     * returned token is used for making the subsequent StytchClient.passwords.resetByEmail() call.
     *
     * Any other link types passed to this method will return a [DeeplinkHandledStatus.NotHandled] class.
     * @param uri intent.data from deep link
     * @param sessionDurationMinutes desired session duration in minutes
     * @param callback A callback that receives a [DeeplinkHandledStatus]
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

    /**
     * A helper function for determining whether the deeplink is intended for Stytch. Useful in contexts where your
     * application makes use of a deeplink coordinator/manager which requires a synchronous determination of whether a
     * given handler can handle a given URL.
     *
     * @param uri intent.data from deep link
     * @return Boolean
     */
    public fun canHandle(uri: Uri): Boolean =
        ConsumerTokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE)) != ConsumerTokenType.UNKNOWN

    private fun maybeClearBadSessionToken() {
        if (sessionStorage.sessionToken?.isBlank() == true) {
            // We accidentally saved a blank session token instead of a null session token. Clear it all out.
            // This fixes a bug introduced with the original PasswordsResetBySession implementation
            sessionStorage.revoke()
        }
    }
}

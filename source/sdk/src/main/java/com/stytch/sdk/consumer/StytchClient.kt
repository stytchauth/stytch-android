package com.stytch.sdk.consumer

import android.content.Context
import android.net.Uri
import com.stytch.sdk.R
import com.stytch.sdk.common.ConfigurationAnalyticsEvent
import com.stytch.sdk.common.ConfigurationManager
import com.stytch.sdk.common.ConfigurationStep
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.DeeplinkTokenPair
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.QUERY_TOKEN
import com.stytch.sdk.common.QUERY_TOKEN_TYPE
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchClientCommon
import com.stytch.sdk.common.StytchClientOptions
import com.stytch.sdk.common.StytchLazyDelegate
import com.stytch.sdk.common.dfp.DFP
import com.stytch.sdk.common.dfp.DFPImpl
import com.stytch.sdk.common.errors.StytchDeeplinkMissingTokenError
import com.stytch.sdk.common.errors.StytchDeeplinkUnkownTokenTypeError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.events.Events
import com.stytch.sdk.common.events.EventsImpl
import com.stytch.sdk.common.network.CommonApi
import com.stytch.sdk.consumer.biometrics.Biometrics
import com.stytch.sdk.consumer.biometrics.BiometricsImpl
import com.stytch.sdk.consumer.biometrics.BiometricsProviderImpl
import com.stytch.sdk.consumer.crypto.CryptoWallet
import com.stytch.sdk.consumer.crypto.CryptoWalletImpl
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
import com.stytch.sdk.consumer.totp.TOTP
import com.stytch.sdk.consumer.totp.TOTPImpl
import com.stytch.sdk.consumer.userManagement.UserAuthenticationFactor
import com.stytch.sdk.consumer.userManagement.UserManagement
import com.stytch.sdk.consumer.userManagement.UserManagementImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * The StytchClient object is your entrypoint to the Stytch Consumer SDK and is how you interact with all of our
 * supported authentication products.
 */
public object StytchClient {
    internal val configurationManager = ConfigurationManager()

    internal lateinit var sessionStorage: ConsumerSessionStorage

    /**
     * Exposes a flow that reports the initialization state of the SDK. You can use this, or the optional callback in
     * the `configure()` method, to know when the Stytch SDK has been fully initialized and is ready for use
     */
    @JvmStatic
    public val isInitialized: StateFlow<Boolean> = configurationManager.isInitialized.asStateFlow()

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param callback An optional callback that is triggered after configuration and initialization has completed
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    @JvmStatic
    public fun configure(context: Context) {
        val publicToken = context.getString(R.string.STYTCH_PUBLIC_TOKEN)
        configure(context, publicToken, StytchClientOptions(), {})
    }

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param options Optional options to configure the StytchClient
     * @param callback An optional callback that is triggered after configuration and initialization has completed
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    @JvmStatic
    public fun configure(
        context: Context,
        options: StytchClientOptions = StytchClientOptions(),
        callback: ((Boolean) -> Unit) = {},
    ) {
        val publicToken = context.getString(R.string.STYTCH_PUBLIC_TOKEN)
        configure(context, publicToken, options, callback)
    }

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param callback An optional callback that is triggered after configuration and initialization has completed
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    @JvmStatic
    public fun configure(
        context: Context,
        callback: ((Boolean) -> Unit) = {},
    ) {
        val publicToken = context.getString(R.string.STYTCH_PUBLIC_TOKEN)
        configure(context, publicToken, StytchClientOptions(), callback)
    }

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param options Optional options to configure the StytchClient
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    @JvmStatic
    public fun configure(
        context: Context,
        options: StytchClientOptions = StytchClientOptions(),
    ) {
        val publicToken = context.getString(R.string.STYTCH_PUBLIC_TOKEN)
        configure(context, publicToken, options, {})
    }

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param callback An optional callback that is triggered after configuration and initialization has completed
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    @JvmStatic
    public fun configure(
        context: Context,
        publicToken: String,
        callback: ((Boolean) -> Unit) = {},
    ) {
        configure(context, publicToken, StytchClientOptions(), callback)
    }

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param options Optional options to configure the StytchClient
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    @JvmStatic
    public fun configure(
        context: Context,
        publicToken: String,
        options: StytchClientOptions = StytchClientOptions(),
    ) {
        configure(context, publicToken, options) {}
    }

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param publicToken Available via the Stytch dashboard in the API keys section
     */
    @JvmStatic
    public fun configure(
        context: Context,
        publicToken: String,
    ) {
        configure(context, publicToken, StytchClientOptions()) {}
    }

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param options Optional options to configure the StytchClient
     * @param callback An optional callback that is triggered after configuration and initialization has completed
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    @JvmStatic
    public fun configure(
        context: Context,
        publicToken: String,
        options: StytchClientOptions = StytchClientOptions(),
        callback: ((Boolean) -> Unit) = {},
    ) {
        configurationManager.configure(
            client = StytchClientCommonConfiguration { callback(true) },
            context = context,
            publicToken = publicToken,
            options = options,
        )
        sessionStorage = ConsumerSessionStorage(StorageHelper)
    }

    /**
     * Exposes an instance of the [MagicLinks] interface whicih provides methods for sending and authenticating users
     * with Email Magic Links.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val magicLinks: MagicLinks by StytchLazyDelegate(StytchApi::assertInitialized) {
        MagicLinksImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.MagicLinks.Email,
            configurationManager.pkcePairManager,
        )
    }

    /**
     * Exposes an instance of the [OTP] interface which provides methods for sending and authenticating
     * One-Time Passcodes (OTP) via SMS, WhatsApp, and Email.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val otps: OTP by StytchLazyDelegate(StytchApi::assertInitialized) {
        OTPImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.OTP,
        )
    }

    /**
     * Exposes an instance of the [Passwords] interface which provides methods for authenticating, creating, resetting,
     * and performing strength checks of passwords.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val passwords: Passwords by StytchLazyDelegate(StytchApi::assertInitialized) {
        PasswordsImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.Passwords,
            configurationManager.pkcePairManager,
        )
    }

    /**
     * Exposes an instance of the [Sessions] interface which provides methods for authenticating, updating, or revoking
     * sessions, and properties to retrieve the existing session token (opaque or JWT).
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val sessions: Sessions by StytchLazyDelegate(StytchApi::assertInitialized) {
        SessionsImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.Sessions,
        )
    }

    /**
     * Exposes an instance of the [Biometrics] interface which provides methods for detecting biometric availability,
     * registering, authenticating, and removing biometrics identifiers.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val biometrics: Biometrics by StytchLazyDelegate(StytchApi::assertInitialized) {
        BiometricsImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StorageHelper,
            StytchApi.Biometrics,
            BiometricsProviderImpl(),
        ) { biometricRegistrationId ->
            user.deleteFactor(UserAuthenticationFactor.BiometricRegistration(biometricRegistrationId))
        }
    }

    /**
     * Exposes an instance of the [UserManagement] interface which provides methods for retrieving an authenticated
     * user and deleting authentication factors from an authenticated user.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val user: UserManagement by StytchLazyDelegate(StytchApi::assertInitialized) {
        UserManagementImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.UserManagement,
        )
    }

    /**
     * Exposes an instance of the [OAuth] interface which provides methods for authenticating a user via a native
     * Google OneTap prompt or any of our supported third-party OAuth providers
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val oauth: OAuth by StytchLazyDelegate(StytchApi::assertInitialized) {
        OAuthImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.OAuth,
            configurationManager.pkcePairManager,
        )
    }

    /**
     * Exposes an instance of the [Passkeys] interface which provides methods for registering and authenticating
     * with Passkeys.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val passkeys: Passkeys by StytchLazyDelegate(StytchApi::assertInitialized) {
        PasskeysImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.WebAuthn,
        )
    }

    /**
     * Exposes an instance of the [DFP] interface which provides a method for retrieving a dfp_telemetry_id for use
     * in DFP lookups on your backend server
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val dfp: DFP by StytchLazyDelegate(StytchApi::assertInitialized) {
        DFPImpl(
            configurationManager.dfpProvider,
            configurationManager.dispatchers,
            configurationManager.externalScope,
        )
    }

    /**
     * Exposes an instance of the [CryptoWallet] interface which provides methods for authenticating with a crypto
     * wallet
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val crypto: CryptoWallet by StytchLazyDelegate(StytchApi::assertInitialized) {
        CryptoWalletImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.Crypto,
        )
    }

    /**
     * Exposes an instance of the [TOTP] interface which provides methods for creating, authenticating, and recovering
     * TOTP codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchClient.configure()
     */
    @JvmStatic
    public val totp: TOTP by StytchLazyDelegate(StytchApi::assertInitialized) {
        TOTPImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchApi.TOTP,
        )
    }

    internal val events: Events by StytchLazyDelegate(StytchApi::assertInitialized) {
        EventsImpl(
            configurationManager.deviceInfo,
            configurationManager.appSessionId,
            configurationManager.externalScope,
            configurationManager.dispatchers,
            StytchApi.Events,
        )
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
    @JvmStatic
    public suspend fun handle(
        uri: Uri,
        sessionDurationMinutes: Int,
    ): DeeplinkHandledStatus {
        StytchApi.assertInitialized()
        return withContext(configurationManager.dispatchers.io) {
            val token = uri.getQueryParameter(QUERY_TOKEN)
            if (token.isNullOrEmpty()) {
                return@withContext DeeplinkHandledStatus.NotHandled(StytchDeeplinkMissingTokenError())
            }
            when (val tokenType = ConsumerTokenType.fromString(uri.getQueryParameter(QUERY_TOKEN_TYPE))) {
                ConsumerTokenType.MAGIC_LINKS -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            magicLinks.authenticate(MagicLinks.AuthParameters(token, sessionDurationMinutes)),
                        ),
                    )
                }
                ConsumerTokenType.OAUTH -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            oauth.authenticate(OAuth.ThirdParty.AuthenticateParameters(token, sessionDurationMinutes)),
                        ),
                    )
                }
                ConsumerTokenType.RESET_PASSWORD -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.ManualHandlingRequired(type = ConsumerTokenType.RESET_PASSWORD, token = token)
                }
                else -> {
                    events.logEvent("deeplink_handled_failure", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.NotHandled(StytchDeeplinkUnkownTokenTypeError())
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
    @JvmStatic
    public fun handle(
        uri: Uri,
        sessionDurationMinutes: Int,
        callback: (response: DeeplinkHandledStatus) -> Unit,
    ) {
        configurationManager.externalScope.launch(configurationManager.dispatchers.ui) {
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
    @JvmStatic
    public fun canHandle(uri: Uri): Boolean =
        ConsumerTokenType.fromString(uri.getQueryParameter(QUERY_TOKEN_TYPE)) != ConsumerTokenType.UNKNOWN

    /**
     * Retrieve the most recently created PKCE code pair from the device, if available
     */
    @JvmStatic
    public fun getPKCECodePair(): PKCECodePair? = configurationManager.pkcePairManager.getPKCECodePair()

    internal fun startSmsRetriever(sessionDurationMinutes: Int) =
        configurationManager.smsRetriever.start(sessionDurationMinutes)

    /**
     * Retrieve the token and a concrete token type from a deeplink
     */
    @JvmStatic
    public fun parseDeeplink(uri: Uri): DeeplinkTokenPair =
        DeeplinkTokenPair(
            tokenType = ConsumerTokenType.fromString(uri.getQueryParameter(QUERY_TOKEN_TYPE)),
            token = uri.getQueryParameter(QUERY_TOKEN),
        )

    /**
     * Retrieve the last used authentication method, if available
     */
    @JvmStatic
    public val lastAuthMethodUsed: ConsumerAuthMethod?
        get() = sessionStorage.lastAuthMethodUsed

    private class StytchClientCommonConfiguration(
        override var onFinishedInitialization: () -> Unit,
    ) : StytchClientCommon {
        override val commonApi: CommonApi = StytchApi

        override fun logEvent(
            eventName: String,
            details: Map<String, Any>?,
            error: Exception?,
        ) = events.logEvent(eventName, details, error)

        override fun rehydrateSession(): Job =
            configurationManager.externalScope.launch(configurationManager.dispatchers.io) {
                val start = Date().time
                sessionStorage.session?.let {
                    println("THIS IS MY SESSION DATA: $it")
                    // if we have a session, it's expiration date has already been validated, now attempt
                    // to validate it with the Stytch servers
                    StytchApi.Sessions.authenticate(null).apply {
                        configurationManager.emitAnalyticsEvent(
                            ConfigurationAnalyticsEvent(
                                step = ConfigurationStep.SESSION_HYDRATION,
                                duration = Date().time - start,
                            ),
                        )
                        launchSessionUpdater(configurationManager.dispatchers, sessionStorage)
                    }
                } ?: configurationManager.emitAnalyticsEvent(
                    ConfigurationAnalyticsEvent(
                        step = ConfigurationStep.SESSION_HYDRATION,
                        duration = Date().time - start,
                    ),
                )
            }

        override fun smsAutofillCallback(
            code: String?,
            sessionDurationMinutes: Int?,
        ) {
            val parsedCode = code ?: return
            val methodId = sessionStorage.methodId ?: return
            configurationManager.externalScope.launch(configurationManager.dispatchers.io) {
                otps.authenticate(
                    OTP.AuthParameters(
                        token = parsedCode,
                        methodId = methodId,
                        sessionDurationMinutes = sessionDurationMinutes ?: DEFAULT_SESSION_TIME_MINUTES,
                    ),
                )
            }
        }

        override fun getSessionToken(): String? = sessionStorage.sessionToken
    }
}

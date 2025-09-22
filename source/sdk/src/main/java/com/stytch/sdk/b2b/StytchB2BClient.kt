package com.stytch.sdk.b2b

import android.content.Context
import android.net.Uri
import com.stytch.sdk.R
import com.stytch.sdk.b2b.discovery.Discovery
import com.stytch.sdk.b2b.discovery.DiscoveryImpl
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksImpl
import com.stytch.sdk.b2b.member.Member
import com.stytch.sdk.b2b.member.MemberImpl
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.oauth.OAuth
import com.stytch.sdk.b2b.oauth.OAuthImpl
import com.stytch.sdk.b2b.organization.Organization
import com.stytch.sdk.b2b.organization.OrganizationImpl
import com.stytch.sdk.b2b.otp.OTP
import com.stytch.sdk.b2b.otp.OTPImpl
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.b2b.passwords.PasswordsImpl
import com.stytch.sdk.b2b.rbac.RBAC
import com.stytch.sdk.b2b.rbac.RBACImpl
import com.stytch.sdk.b2b.recoveryCodes.RecoveryCodes
import com.stytch.sdk.b2b.recoveryCodes.RecoveryCodesImpl
import com.stytch.sdk.b2b.scim.SCIM
import com.stytch.sdk.b2b.scim.SCIMImpl
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.b2b.searchManager.SearchManagerImpl
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.b2b.sessions.B2BSessions
import com.stytch.sdk.b2b.sessions.B2BSessionsImpl
import com.stytch.sdk.b2b.sso.SSO
import com.stytch.sdk.b2b.sso.SSOImpl
import com.stytch.sdk.b2b.totp.TOTP
import com.stytch.sdk.b2b.totp.TOTPImpl
import com.stytch.sdk.common.ConfigurationAnalyticsEvent
import com.stytch.sdk.common.ConfigurationManager
import com.stytch.sdk.common.ConfigurationStep
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.DeeplinkTokenPair
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.QUERY_REDIRECT_TYPE
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
import com.stytch.sdk.common.network.models.Vertical
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * The StytchB2BClient object is your entrypoint to the Stytch B2B SDK and is how you interact with all of our
 * supported authentication products.
 */

public object StytchB2BClient {
    internal val configurationManager = ConfigurationManager()

    internal lateinit var sessionStorage: B2BSessionStorage

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
     * @param options Optional options to configure the StytchB2BClient
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
        // We must initialize the storagehelper/sessionstorage before configuration, because configuration kicks
        // off a task that relies on the storage being available (session hydration). HOWEVER, we don't want to handle
        // any exception in storage initialization immediately, because we want to log the error to the Events API,
        // which requires the API to be configured (which happens in the configure call). So, catch and hold any error
        // until _after_ configuration completes, at which point it is safe to log it.
        runCatching {
            val storageHelperInitializationJob = StorageHelper.initialize(context)
            sessionStorage = B2BSessionStorage(StorageHelper)
            configurationManager.configure(
                client =
                    StytchB2BClientCommonConfiguration {
                        sessionStorage.emitCurrent()
                        callback(true)
                    },
                context = context,
                publicToken = publicToken,
                options = options,
                storageHelperInitializationJob = storageHelperInitializationJob,
            )
        }.onFailure {
            val error =
                StytchInternalError(
                    message = "Failed to initialize the SDK",
                    exception = it,
                )
            events.logEvent("client_initialization_failure", null, error)
            throw error
        }
    }

    /**
     * Exposes an instance of the [B2BMagicLinks] interface whicih provides methods for sending and authenticating
     * users with Email Magic Links.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val magicLinks: B2BMagicLinks by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        B2BMagicLinksImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.MagicLinks.Email,
            StytchB2BApi.MagicLinks.Discovery,
            configurationManager.pkcePairManager,
        )
    }

    /**
     * Exposes an instance of the [B2BSessions] interface which provides methods for authenticating, updating, or
     * revoking sessions, and properties to retrieve the existing session token (opaque or JWT).
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val sessions: B2BSessions by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        B2BSessionsImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.Sessions,
        )
    }

    /**
     * Exposes an instance of the [Organization] interface which provides methods for retrieving the current
     * authenticated user's organization.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val organization: Organization by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        OrganizationImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.Organization,
        )
    }

    /**
     * Exposes an instance of the [Member] interface which provides methods for retrieving the current authenticated
     * user.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val member: Member by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        MemberImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.Member,
        )
    }

    /**
     * Exposes an instance of the [Passwords] interface which provides methods for authenticating passwords, resetting
     * passwords, and checking the strength of passwords
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val passwords: Passwords by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        PasswordsImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.Passwords,
            StytchB2BApi.Passwords.Discovery,
            configurationManager.pkcePairManager,
        )
    }

    /**
     * Exposes an instance of the [Discovery] interface which provides methods for creating and discovering
     * Organizations and exchanging sessions between organizations
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val discovery: Discovery by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        DiscoveryImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.Discovery,
        )
    }

    /**
     * Exposes an instance of the [SSO] interface which provides methods for authenticating SSO sessions
     */
    @JvmStatic
    public val sso: SSO by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        SSOImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.SSO,
            configurationManager.pkcePairManager,
        )
    }

    /**
     * Exposes an instance of the [DFP] interface which provides a method for retrieving a dfp_telemetry_id for use
     * in DFP lookups on your backend server
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val dfp: DFP by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        DFPImpl(
            configurationManager.dfpProvider,
            configurationManager.dispatchers,
            configurationManager.externalScope,
        )
    }

    internal val events: Events by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        EventsImpl(
            configurationManager.deviceInfo,
            configurationManager.appSessionId,
            configurationManager.externalScope,
            configurationManager.dispatchers,
            StytchB2BApi.Events,
        )
    }

    /**
     * Exposes an instance of the [OTP] interface which provides a method for sending and authenticating OTP codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val otp: OTP by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        OTPImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.OTP,
        )
    }

    /**
     * Exposes an instance of the [TOTP] interface which provides a method for creating and authenticating TOTP codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val totp: TOTP by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        TOTPImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.TOTP,
        )
    }

    /**
     * Exposes an instance of the [RecoveryCodes] interface which provides methods for getting, rotating, and
     * recovering recovery codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val recoveryCodes: RecoveryCodes by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        RecoveryCodesImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.RecoveryCodes,
        )
    }

    /**
     * Exposes an instance of the [OAuth] interface which provides a method for starting and authenticating OAuth and
     * OAuth Discovery flows
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val oauth: OAuth by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        OAuthImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
            StytchB2BApi.OAuth,
            configurationManager.pkcePairManager,
        )
    }

    /**
     * Exposes an instance of the [RBAC] interface which provides methods for checking a member's permissions
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val rbac: RBAC by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        RBACImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            sessionStorage,
        )
    }

    /**
     * Exposes an instance of the [SearchManager] interface which provides methods to search organizations and members
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val searchManager: SearchManager by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        SearchManagerImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            StytchB2BApi.SearchManager,
        )
    }

    /**
     * Exposes an instance of the [SCIM] interface which provides methods for creating, getting, updating, deleting, and
     * rotating SCIM connections
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val scim: SCIM by StytchLazyDelegate(StytchB2BApi::assertInitialized) {
        SCIMImpl(
            configurationManager.externalScope,
            configurationManager.dispatchers,
            StytchB2BApi.SCIM,
        )
    }

    /**
     * Call this method to parse out and authenticate deeplinks that your application receives. The currently supported
     * deeplink types are: B2B Email Magic Links.
     *
     * For B2B Email Magic Links, it will return a [DeeplinkHandledStatus.Handled] class containing either the
     * authenticated response or error.
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
        StytchB2BApi.assertInitialized()
        val (tokenType, token, redirectType) = parseDeeplink(uri)
        if (token.isNullOrEmpty()) {
            return DeeplinkHandledStatus.NotHandled(StytchDeeplinkMissingTokenError())
        }
        return withContext(configurationManager.dispatchers.io) {
            when (tokenType) {
                B2BTokenType.MULTI_TENANT_MAGIC_LINKS -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            magicLinks.authenticate(B2BMagicLinks.AuthParameters(token, sessionDurationMinutes)),
                        ),
                    )
                }
                B2BTokenType.DISCOVERY -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    if (redirectType == B2BRedirectType.RESET_PASSWORD) {
                        return@withContext DeeplinkHandledStatus.ManualHandlingRequired(type = tokenType, token = token)
                    }
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Discovery(
                            magicLinks.discoveryAuthenticate(
                                B2BMagicLinks.DiscoveryAuthenticateParameters(
                                    token = token,
                                ),
                            ),
                        ),
                    )
                }
                B2BTokenType.MULTI_TENANT_PASSWORDS -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.ManualHandlingRequired(type = tokenType, token = token)
                }
                B2BTokenType.SSO -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            sso.authenticate(
                                SSO.AuthenticateParams(
                                    ssoToken = token,
                                    sessionDurationMinutes = sessionDurationMinutes,
                                ),
                            ),
                        ),
                    )
                }
                B2BTokenType.OAUTH -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            oauth.authenticate(
                                OAuth.AuthenticateParameters(
                                    oauthToken = token,
                                    sessionDurationMinutes = sessionDurationMinutes,
                                ),
                            ),
                        ),
                    )
                }
                B2BTokenType.DISCOVERY_OAUTH -> {
                    events.logEvent("deeplink_handled_success", details = mapOf("token_type" to tokenType))
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Discovery(
                            oauth.discovery.authenticate(
                                OAuth.Discovery.DiscoveryAuthenticateParameters(
                                    discoveryOauthToken = token,
                                ),
                            ),
                        ),
                    )
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
     * deeplink types are: B2B Email Magic Links.
     *
     * For B2B Email Magic Links, it will return a [DeeplinkHandledStatus.Handled] class containing either the
     * authenticated response or error.
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
        B2BTokenType.fromString(uri.getQueryParameter(QUERY_TOKEN_TYPE)) != B2BTokenType.UNKNOWN

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
            tokenType = B2BTokenType.fromString(uri.getQueryParameter(QUERY_TOKEN_TYPE)),
            token = uri.getQueryParameter(QUERY_TOKEN),
            redirectType = B2BRedirectType.fromString(uri.getQueryParameter(QUERY_REDIRECT_TYPE)),
        )

    /**
     * Retrieve the last used authentication method, if available
     */
    @JvmStatic
    public val lastAuthMethodUsed: B2BAuthMethod?
        get() = sessionStorage.lastAuthMethodUsed

    private class StytchB2BClientCommonConfiguration(
        override var onFinishedInitialization: () -> Unit,
    ) : StytchClientCommon {
        override val commonApi: CommonApi = StytchB2BApi

        override val expectedVertical: Vertical = Vertical.B2B

        override fun logEvent(
            eventName: String,
            details: Map<String, Any>?,
            error: Exception?,
        ) = events.logEvent(eventName, details, error)

        override fun rehydrateSession(): Job =
            configurationManager.externalScope.launch(configurationManager.dispatchers.io) {
                val start = Date().time
                sessionStorage.memberSession?.let {
                    // if we have a session, it's expiration date has already been validated, now attempt
                    // to validate it with the Stytch servers
                    sessions
                        .authenticate(
                            B2BSessions.AuthParams(
                                if (configurationManager.options.enableAutomaticSessionExtension) {
                                    configurationManager.options.sessionDurationMinutes
                                } else {
                                    null
                                },
                            ),
                        ).apply {
                            configurationManager.emitAnalyticsEvent(
                                ConfigurationAnalyticsEvent(
                                    step = ConfigurationStep.SESSION_HYDRATION,
                                    duration = Date().time - start,
                                ),
                            )
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
            val organizationId = sessionStorage.organization?.organizationId ?: return
            val memberId = sessionStorage.member?.memberId ?: return
            val parsedCode = code ?: return
            configurationManager.externalScope.launch(configurationManager.dispatchers.io) {
                otp.sms.authenticate(
                    OTP.SMS.AuthenticateParameters(
                        organizationId = organizationId,
                        memberId = memberId,
                        code = parsedCode,
                        sessionDurationMinutes =
                            sessionDurationMinutes ?: configurationManager.options.sessionDurationMinutes,
                    ),
                )
            }
        }

        override fun getSessionToken(): String? = sessionStorage.sessionToken
    }
}

package com.stytch.sdk.b2b

import android.app.Application
import android.content.Context
import android.net.Uri
import com.stytch.sdk.b2b.discovery.Discovery
import com.stytch.sdk.b2b.discovery.DiscoveryImpl
import com.stytch.sdk.b2b.extensions.launchSessionUpdater
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
import com.stytch.sdk.common.AppLifecycleListener
import com.stytch.sdk.common.DEFAULT_SESSION_TIME_MINUTES
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.DeeplinkTokenPair
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.NetworkChangeListener
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.QUERY_REDIRECT_TYPE
import com.stytch.sdk.common.QUERY_TOKEN
import com.stytch.sdk.common.QUERY_TOKEN_TYPE
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchClientOptions
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchLazyDelegate
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.ActivityProvider
import com.stytch.sdk.common.dfp.CaptchaProviderImpl
import com.stytch.sdk.common.dfp.DFP
import com.stytch.sdk.common.dfp.DFPImpl
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.dfp.DFPProviderImpl
import com.stytch.sdk.common.errors.StytchDeeplinkMissingTokenError
import com.stytch.sdk.common.errors.StytchDeeplinkUnkownTokenTypeError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.events.Events
import com.stytch.sdk.common.events.EventsImpl
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.pkcePairManager.PKCEPairManagerImpl
import com.stytch.sdk.common.smsRetriever.StytchSMSRetriever
import com.stytch.sdk.common.smsRetriever.StytchSMSRetrieverImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import java.lang.ref.WeakReference
import java.util.UUID

/**
 * The StytchB2BClient object is your entrypoint to the Stytch B2B SDK and is how you interact with all of our
 * supported authentication products.
 */
public object StytchB2BClient {
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal var externalScope: CoroutineScope = CoroutineScope(SupervisorJob())
    internal lateinit var sessionStorage: B2BSessionStorage
    internal var pkcePairManager: PKCEPairManager = PKCEPairManagerImpl(StorageHelper, EncryptionManager)
    internal lateinit var dfpProvider: DFPProvider
    internal var bootstrapData: BootstrapData = BootstrapData()
    internal lateinit var publicToken: String

    private var _isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private lateinit var smsRetriever: StytchSMSRetriever

    /**
     * Exposes a flow that reports the initialization state of the SDK. You can use this, or the optional callback in
     * the `configure()` method, to know when the Stytch SDK has been fully initialized and is ready for use
     */
    @JvmStatic
    public val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    @VisibleForTesting
    internal lateinit var deviceInfo: DeviceInfo

    @VisibleForTesting
    internal lateinit var appSessionId: String

    private var stytchClientOptions: StytchClientOptions? = null

    private var applicationContext = WeakReference<Context>(null)

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
        if (::publicToken.isInitialized && publicToken == this.publicToken && options == this.stytchClientOptions) {
            return callback(true)
        }
        try {
            applicationContext = WeakReference(context.applicationContext)
            deviceInfo = context.getDeviceInfo()
            this.publicToken = publicToken
            this.stytchClientOptions = options
            appSessionId = "app-session-id-${UUID.randomUUID()}"
            StorageHelper.initialize(context)
            sessionStorage = B2BSessionStorage(StorageHelper)
            StytchB2BApi.configure(publicToken, deviceInfo)
            val activityProvider = ActivityProvider(context.applicationContext as Application)
            dfpProvider =
                DFPProviderImpl(
                    publicToken = publicToken,
                    dfppaDomain = options.endpointOptions.dfppaDomain,
                    activityProvider = activityProvider,
                )
            configureSmsRetriever(context.applicationContext)
            NetworkChangeListener.configure(context.applicationContext, ::refreshBootstrapAndAPIClient)
            AppLifecycleListener.configureCallback(::refreshBootstrapAndAPIClient)
            externalScope.launch(dispatchers.io) {
                // if there are session identifiers on device start the auto updater to ensure it is still valid
                if (sessionStorage.persistedSessionIdentifiersExist) {
                    sessionStorage.memberSession?.let {
                        // if we have a session, it's expiration date has already been validated, now attempt
                        // to validate it with the Stytch servers
                        StytchB2BApi.Sessions.authenticate(null).apply {
                            launchSessionUpdater(dispatchers, sessionStorage)
                        }
                    }
                }
                _isInitialized.value = true
                events.logEvent("client_initialization_success")
                callback(_isInitialized.value)
            }
        } catch (ex: Exception) {
            events.logEvent("client_initialization_failure", null, ex)
            throw StytchInternalError(
                message = "Failed to initialize the SDK",
                exception = ex,
            )
        }
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

    private fun configureSmsRetriever(applicationContext: Context) {
        smsRetriever =
            StytchSMSRetrieverImpl(applicationContext) { code, sessionDurationMinutes ->
                smsRetriever.finish()
                val organizationId = sessionStorage.organization?.organizationId ?: return@StytchSMSRetrieverImpl
                val memberId = sessionStorage.member?.memberId ?: return@StytchSMSRetrieverImpl
                val parsedCode = code ?: return@StytchSMSRetrieverImpl
                externalScope.launch {
                    otp.sms.authenticate(
                        OTP.SMS.AuthenticateParameters(
                            organizationId = organizationId,
                            memberId = memberId,
                            code = parsedCode,
                            sessionDurationMinutes = sessionDurationMinutes ?: DEFAULT_SESSION_TIME_MINUTES,
                        ),
                    )
                }
            }
    }

    private fun refreshBootstrapAndAPIClient() {
        if (NetworkChangeListener.networkIsAvailable) {
            applicationContext.get()?.let {
                externalScope.launch(dispatchers.io) {
                    refreshBootstrapData()
                    StytchB2BApi.configureDFP(
                        dfpProvider = dfpProvider,
                        captchaProvider =
                            CaptchaProviderImpl(
                                it.applicationContext as Application,
                                externalScope,
                                bootstrapData.captchaSettings.siteKey,
                            ),
                        bootstrapData.dfpProtectedAuthEnabled,
                        bootstrapData.dfpProtectedAuthMode ?: DFPProtectedAuthMode.OBSERVATION,
                    )
                }
            }
        }
    }

    internal suspend fun refreshBootstrapData() {
        bootstrapData =
            when (val res = StytchB2BApi.getBootstrapData()) {
                is StytchResult.Success -> res.value
                else -> BootstrapData()
            }
    }

    internal fun assertInitialized() {
        if (!StytchB2BApi.isInitialized || !::sessionStorage.isInitialized) {
            throw StytchSDKNotConfiguredError("StytchB2BClient")
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
    public val magicLinks: B2BMagicLinks by StytchLazyDelegate(::assertInitialized) {
        B2BMagicLinksImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.MagicLinks.Email,
            StytchB2BApi.MagicLinks.Discovery,
            pkcePairManager,
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
    public val sessions: B2BSessions by StytchLazyDelegate(::assertInitialized) {
        B2BSessionsImpl(
            externalScope,
            dispatchers,
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
    public val organization: Organization by StytchLazyDelegate(::assertInitialized) {
        OrganizationImpl(
            externalScope,
            dispatchers,
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
    public val member: Member by StytchLazyDelegate(::assertInitialized) {
        MemberImpl(
            externalScope,
            dispatchers,
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
    public val passwords: Passwords by StytchLazyDelegate(::assertInitialized) {
        PasswordsImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.Passwords,
            StytchB2BApi.Passwords.Discovery,
            pkcePairManager,
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
    public val discovery: Discovery by StytchLazyDelegate(::assertInitialized) {
        DiscoveryImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.Discovery,
        )
    }

    /**
     * Exposes an instance of the [SSO] interface which provides methods for authenticating SSO sessions
     */
    @JvmStatic
    public val sso: SSO by StytchLazyDelegate(::assertInitialized) {
        SSOImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.SSO,
            pkcePairManager,
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
    public val dfp: DFP by StytchLazyDelegate(::assertInitialized) {
        DFPImpl(dfpProvider, dispatchers, externalScope)
    }

    internal val events: Events by StytchLazyDelegate(::assertInitialized) {
        EventsImpl(deviceInfo, appSessionId, externalScope, dispatchers, StytchB2BApi.Events)
    }

    /**
     * Exposes an instance of the [OTP] interface which provides a method for sending and authenticating OTP codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val otp: OTP by StytchLazyDelegate(::assertInitialized) {
        OTPImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.OTP)
    }

    /**
     * Exposes an instance of the [TOTP] interface which provides a method for creating and authenticating TOTP codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val totp: TOTP by StytchLazyDelegate(::assertInitialized) {
        TOTPImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.TOTP)
    }

    /**
     * Exposes an instance of the [RecoveryCodes] interface which provides methods for getting, rotating, and
     * recovering recovery codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val recoveryCodes: RecoveryCodes by StytchLazyDelegate(::assertInitialized) {
        RecoveryCodesImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.RecoveryCodes)
    }

    /**
     * Exposes an instance of the [OAuth] interface which provides a method for starting and authenticating OAuth and
     * OAuth Discovery flows
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val oauth: OAuth by StytchLazyDelegate(::assertInitialized) {
        OAuthImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.OAuth, pkcePairManager)
    }

    /**
     * Exposes an instance of the [RBAC] interface which provides methods for checking a member's permissions
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val rbac: RBAC by StytchLazyDelegate(::assertInitialized) {
        RBACImpl(externalScope, dispatchers, sessionStorage)
    }

    /**
     * Exposes an instance of the [SearchManager] interface which provides methods to search organizations and members
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val searchManager: SearchManager by StytchLazyDelegate(::assertInitialized) {
        SearchManagerImpl(externalScope, dispatchers, StytchB2BApi.SearchManager)
    }

    /**
     * Exposes an instance of the [SCIM] interface which provides methods for creating, getting, updating, deleting, and
     * rotating SCIM connections
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    @JvmStatic
    public val scim: SCIM by StytchLazyDelegate(::assertInitialized) {
        SCIMImpl(externalScope, dispatchers, StytchB2BApi.SCIM)
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
        assertInitialized()
        val (tokenType, token, redirectType) = parseDeeplink(uri)
        if (token.isNullOrEmpty()) {
            return DeeplinkHandledStatus.NotHandled(StytchDeeplinkMissingTokenError())
        }
        return withContext(dispatchers.io) {
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
    @JvmStatic
    public fun canHandle(uri: Uri): Boolean =
        B2BTokenType.fromString(uri.getQueryParameter(QUERY_TOKEN_TYPE)) != B2BTokenType.UNKNOWN

    /**
     * Retrieve the most recently created PKCE code pair from the device, if available
     */
    @JvmStatic
    public fun getPKCECodePair(): PKCECodePair? = pkcePairManager.getPKCECodePair()

    internal fun startSmsRetriever(sessionDurationMinutes: Int) = smsRetriever.start(sessionDurationMinutes)

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
}

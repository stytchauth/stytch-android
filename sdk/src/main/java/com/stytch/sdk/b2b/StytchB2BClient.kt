package com.stytch.sdk.b2b

import android.app.Application
import android.content.Context
import android.net.Uri
import com.stytch.sdk.b2b.discovery.Discovery
import com.stytch.sdk.b2b.discovery.DiscoveryImpl
import com.stytch.sdk.b2b.events.Events
import com.stytch.sdk.b2b.events.EventsImpl
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
import com.stytch.sdk.b2b.searchManager.SearchManager
import com.stytch.sdk.b2b.searchManager.SearchManagerImpl
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.b2b.sessions.B2BSessions
import com.stytch.sdk.b2b.sessions.B2BSessionsImpl
import com.stytch.sdk.b2b.sso.SSO
import com.stytch.sdk.b2b.sso.SSOImpl
import com.stytch.sdk.b2b.totp.TOTP
import com.stytch.sdk.b2b.totp.TOTPImpl
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.EncryptionManager
import com.stytch.sdk.common.PKCECodePair
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchClientOptions
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.CaptchaProviderImpl
import com.stytch.sdk.common.dfp.DFP
import com.stytch.sdk.common.dfp.DFPImpl
import com.stytch.sdk.common.dfp.DFPProvider
import com.stytch.sdk.common.dfp.DFPProviderImpl
import com.stytch.sdk.common.errors.StytchDeeplinkMissingTokenError
import com.stytch.sdk.common.errors.StytchDeeplinkUnkownTokenTypeError
import com.stytch.sdk.common.errors.StytchInternalError
import com.stytch.sdk.common.errors.StytchSDKNotConfiguredError
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.network.models.DFPProtectedAuthMode
import com.stytch.sdk.common.pkcePairManager.PKCEPairManager
import com.stytch.sdk.common.pkcePairManager.PKCEPairManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID

/**
 * The StytchB2BClient object is your entrypoint to the Stytch B2B SDK and is how you interact with all of our
 * supported authentication products.
 */
public object StytchB2BClient {
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal var externalScope: CoroutineScope = GlobalScope // TODO: SDK-614
    internal val sessionStorage = B2BSessionStorage(StorageHelper, externalScope)
    internal var pkcePairManager: PKCEPairManager = PKCEPairManagerImpl(StorageHelper, EncryptionManager)
    public var bootstrapData: BootstrapData = BootstrapData()
        internal set
    internal lateinit var dfpProvider: DFPProvider

    /**
     * Exposes a flow that reports the initialization state of the SDK. You can use this, or the optional callback in
     * the `configure()` method, to know when the Stytch SDK has been fully initialized and is ready for use
     */
    private var _isInitialized: MutableStateFlow<Boolean> = MutableStateFlow(false)
    public val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    @VisibleForTesting
    internal lateinit var deviceInfo: DeviceInfo

    @VisibleForTesting
    internal lateinit var appSessionId: String

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param callback An optional callback that is triggered after configuration and initialization has completed
     * @throws StytchInternalError - if we failed to initialize for any reason
     */
    public fun configure(
        context: Context,
        publicToken: String,
        options: StytchClientOptions = StytchClientOptions(),
        callback: ((Boolean) -> Unit) = {},
    ) {
        try {
            deviceInfo = context.getDeviceInfo()
            appSessionId = "app-session-id-${UUID.randomUUID()}"
            StorageHelper.initialize(context)
            StytchB2BApi.configure(publicToken, deviceInfo)
            dfpProvider =
                DFPProviderImpl(
                    context = context.applicationContext,
                    publicToken = publicToken,
                    dfppaDomain = options.endpointOptions.dfppaDomain,
                )
            externalScope.launch(dispatchers.io) {
                refreshBootstrapData()
                StytchB2BApi.configureDFP(
                    dfpProvider = dfpProvider,
                    captchaProvider =
                        CaptchaProviderImpl(
                            context.applicationContext as Application,
                            externalScope,
                            bootstrapData.captchaSettings.siteKey,
                        ),
                    bootstrapData.dfpProtectedAuthEnabled,
                    bootstrapData.dfpProtectedAuthMode ?: DFPProtectedAuthMode.OBSERVATION,
                )
                // if there are session identifiers on device start the auto updater to ensure it is still valid
                if (sessionStorage.persistedSessionIdentifiersExist) {
                    StytchB2BApi.Sessions.authenticate(null).apply {
                        launchSessionUpdater(dispatchers, sessionStorage)
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

    public suspend fun refreshBootstrapData() {
        bootstrapData =
            when (val res = StytchB2BApi.getBootstrapData()) {
                is StytchResult.Success -> res.value
                else -> BootstrapData()
            }
    }

    internal fun assertInitialized() {
        if (!StytchB2BApi.isInitialized) {
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
    public var magicLinks: B2BMagicLinks =
        B2BMagicLinksImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.MagicLinks.Email,
            StytchB2BApi.MagicLinks.Discovery,
            pkcePairManager,
        )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [B2BSessions] interface which provides methods for authenticating, updating, or
     * revoking sessions, and properties to retrieve the existing session token (opaque or JWT).
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var sessions: B2BSessions =
        B2BSessionsImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.Sessions,
        )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [Organization] interface which provides methods for retrieving the current
     * authenticated user's organization.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var organization: Organization =
        OrganizationImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.Organization,
        )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [Member] interface which provides methods for retrieving the current authenticated
     * user.
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var member: Member =
        MemberImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.Member,
        )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [Passwords] interface which provides methods for authenticating passwords, resetting
     * passwords, and checking the strength of passwords
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var passwords: Passwords =
        PasswordsImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.Passwords,
            pkcePairManager,
        )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [Discovery] interface which provides methods for creating and discovering
     * Organizations and exchanging sessions between organizations
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var discovery: Discovery =
        DiscoveryImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.Discovery,
        )
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [SSO] interface which provides methods for authenticating SSO sessions
     */
    public var sso: SSO =
        SSOImpl(
            externalScope,
            dispatchers,
            sessionStorage,
            StytchB2BApi.SSO,
            pkcePairManager,
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
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public val dfp: DFP by lazy {
        assertInitialized()
        DFPImpl(dfpProvider, dispatchers, externalScope)
    }

    public val events: Events
        get() {
            assertInitialized()
            return EventsImpl(deviceInfo, appSessionId, externalScope, dispatchers, StytchB2BApi.Events)
        }

    /**
     * Exposes an instance of the [OTP] interface which provides a method for sending and authenticating OTP codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var otp: OTP = OTPImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.OTP)
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [TOTP] interface which provides a method for creating and authenticating TOTP codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var totp: TOTP = TOTPImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.TOTP)
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [RecoveryCodes] interface which provides methods for getting, rotating, and
     * recovering recovery codes
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var recoveryCodes: RecoveryCodes =
        RecoveryCodesImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.RecoveryCodes)
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [OAuth] interface which provides a method for starting and authenticating OAuth and
     * OAuth Discovery flows
     *
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var oauth: OAuth =
        OAuthImpl(externalScope, dispatchers, sessionStorage, StytchB2BApi.OAuth, pkcePairManager)
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [RBAC] interface which provides methods for checking a member's permissions
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var rbac: RBAC = RBACImpl(externalScope, dispatchers, sessionStorage)
        get() {
            assertInitialized()
            return field
        }
        internal set

    /**
     * Exposes an instance of the [SearchManager] interface which provides methods for search organizations and members
     * @throws [StytchSDKNotConfiguredError] if you attempt to access this property before calling
     * StytchB2BClient.configure()
     */
    public var searchManager: SearchManager = SearchManagerImpl(externalScope, dispatchers, StytchB2BApi.SearchManager)
        get() {
            assertInitialized()
            return field
        }
        internal set

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
    public suspend fun handle(
        uri: Uri,
        sessionDurationMinutes: UInt,
    ): DeeplinkHandledStatus {
        assertInitialized()
        return withContext(dispatchers.io) {
            val token = uri.getQueryParameter(Constants.QUERY_TOKEN)
            if (token.isNullOrEmpty()) {
                return@withContext DeeplinkHandledStatus.NotHandled(StytchDeeplinkMissingTokenError())
            }
            when (val tokenType = B2BTokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE))) {
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
    public fun handle(
        uri: Uri,
        sessionDurationMinutes: UInt,
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
    public fun canHandle(uri: Uri): Boolean =
        B2BTokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE)) != B2BTokenType.UNKNOWN

    /**
     * Retrieve the most recently created PKCE code pair from the device, if available
     */
    public fun getPKCECodePair(): PKCECodePair? = pkcePairManager.getPKCECodePair()
}

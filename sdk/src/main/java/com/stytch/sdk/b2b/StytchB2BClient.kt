package com.stytch.sdk.b2b

import android.app.Application
import android.content.Context
import android.net.Uri
import com.stytch.sdk.b2b.discovery.Discovery
import com.stytch.sdk.b2b.discovery.DiscoveryImpl
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksImpl
import com.stytch.sdk.b2b.member.Member
import com.stytch.sdk.b2b.member.MemberImpl
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.organization.Organization
import com.stytch.sdk.b2b.organization.OrganizationImpl
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.b2b.passwords.PasswordsImpl
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.b2b.sessions.B2BSessions
import com.stytch.sdk.b2b.sessions.B2BSessionsImpl
import com.stytch.sdk.b2b.sso.SSO
import com.stytch.sdk.b2b.sso.SSOImpl
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.DeeplinkResponse
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.dfp.ActivityProvider
import com.stytch.sdk.common.dfp.CaptchaProviderImpl
import com.stytch.sdk.common.dfp.DFPProviderImpl
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.common.network.models.BootstrapData
import com.stytch.sdk.common.stytchError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The StytchB2BClient object is your entrypoint to the Stytch B2B SDK and is how you interact with all of our
 * supported authentication products.
 */
public object StytchB2BClient {
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal val sessionStorage = B2BSessionStorage(StorageHelper)
    internal var externalScope: CoroutineScope = GlobalScope // TODO: SDK-614
    public var bootstrapData: BootstrapData = BootstrapData()
        internal set

    /**
     * This configures the API for authenticating requests and the encrypted storage helper for persisting session data
     * across app launches.
     * You must call this method before making any Stytch authentication requests.
     * @param context The applicationContext of your app
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @throws StytchExceptions.Critical - if we failed to generate new encryption keys
     */
    public fun configure(context: Context, publicToken: String) {
        try {
            val deviceInfo = context.getDeviceInfo()
            StorageHelper.initialize(context)
            StytchB2BApi.configure(publicToken, deviceInfo)
            val activityProvider = ActivityProvider(context.applicationContext as Application)
            externalScope.launch(dispatchers.io) {
                bootstrapData = when (val res = StytchB2BApi.getBootstrapData()) {
                    is StytchResult.Success -> res.value
                    else -> BootstrapData()
                }
                if (bootstrapData.dfpProtectedAuthEnabled) {
                    StytchB2BApi.configureDFP(
                        dfpProvider = DFPProviderImpl(publicToken, activityProvider),
                        captchaProvider = CaptchaProviderImpl(
                            context.applicationContext as Application,
                            externalScope,
                            bootstrapData.captchaSettings.siteKey
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            throw StytchExceptions.Critical(ex)
        }
    }

    @Suppress("MaxLineLength")
    internal fun assertInitialized() {
        if (!StytchB2BApi.isInitialized) {
            stytchError(
                "StytchB2BClient not configured. You must call 'StytchB2BClient.configure(...)' before using any functionality of the StytchB2BClient." // ktlint-disable max-line-length
            )
        }
    }

    /**
     * Exposes an instance of the [B2BMagicLinks] interface whicih provides methods for sending and authenticating
     * users with Email Magic Links.
     *
     * @throws [stytchError] if you attempt to access this property before calling StytchB2BClient.configure()
     */
    public var magicLinks: B2BMagicLinks = B2BMagicLinksImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchB2BApi.MagicLinks.Email,
        StytchB2BApi.MagicLinks.Discovery,
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
     * @throws [stytchError] if you attempt to access this property before calling StytchB2BClient.configure()
     */
    public var sessions: B2BSessions = B2BSessionsImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StytchB2BApi.Sessions
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
     * @throws [stytchError] if you attempt to access this property before calling StytchB2BClient.configure()
     */
    public var organization: Organization = OrganizationImpl(
        externalScope,
        dispatchers,
        StytchB2BApi.Organization
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
     * @throws [stytchError] if you attempt to access this property before calling StytchB2BClient.configure()
     */
    public var member: Member = MemberImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StytchB2BApi.Member
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
     * @throws [stytchError] if you attempt to access this property before calling StytchB2BClient.configure()
     */
    public var passwords: Passwords = PasswordsImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchB2BApi.Passwords,
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
     * @throws [stytchError] if you attempt to access this property before calling StytchB2BClient.configure()
     */
    public var discovery: Discovery = DiscoveryImpl(
        externalScope,
        dispatchers,
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
    public var sso: SSO = SSOImpl(
        externalScope,
        dispatchers,
        sessionStorage,
        StorageHelper,
        StytchB2BApi.SSO,
    )
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
    public suspend fun handle(uri: Uri, sessionDurationMinutes: UInt): DeeplinkHandledStatus {
        assertInitialized()
        return withContext(dispatchers.io) {
            val token = uri.getQueryParameter(Constants.QUERY_TOKEN)
            if (token.isNullOrEmpty()) {
                return@withContext DeeplinkHandledStatus.NotHandled(StytchErrorType.DEEPLINK_MISSING_TOKEN.message)
            }
            when (val tokenType = B2BTokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE))) {
                B2BTokenType.MULTI_TENANT_MAGIC_LINKS -> {
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            magicLinks.authenticate(B2BMagicLinks.AuthParameters(token, sessionDurationMinutes))
                        )
                    )
                }
                B2BTokenType.DISCOVERY -> {
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Discovery(
                            magicLinks.discoveryAuthenticate(
                                B2BMagicLinks.DiscoveryAuthenticateParameters(
                                    token = token
                                )
                            )
                        )
                    )
                }
                B2BTokenType.MULTI_TENANT_PASSWORDS -> {
                    DeeplinkHandledStatus.ManualHandlingRequired(type = tokenType, token = token)
                }
                B2BTokenType.SSO -> {
                    DeeplinkHandledStatus.Handled(
                        DeeplinkResponse.Auth(
                            sso.authenticate(
                                SSO.AuthenticateParams(
                                    ssoToken = token,
                                    sessionDurationMinutes = sessionDurationMinutes
                                )
                            )
                        )
                    )
                }
                else -> {
                    DeeplinkHandledStatus.NotHandled(StytchErrorType.DEEPLINK_UNKNOWN_TOKEN.message)
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
        B2BTokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE)) != B2BTokenType.UNKNOWN
}

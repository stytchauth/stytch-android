package com.stytch.sdk.b2b

import android.content.Context
import android.net.Uri
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinksImpl
import com.stytch.sdk.b2b.member.Member
import com.stytch.sdk.b2b.member.MemberImpl
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.organization.Organization
import com.stytch.sdk.b2b.organization.OrganizationImpl
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.b2b.sessions.B2BSessions
import com.stytch.sdk.b2b.sessions.B2BSessionsImpl
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.DeeplinkHandledStatus
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.TokenType
import com.stytch.sdk.common.extensions.getDeviceInfo
import com.stytch.sdk.common.network.StytchErrorType
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
            StytchB2BApi.configure(publicToken, deviceInfo)
            StorageHelper.initialize(context)
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
        StytchB2BApi.MagicLinks.Email
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
     * @throws [stytchError] if you attempt to access this property before calling StytchB2BClient.configure()
     */
    public var member: Member = MemberImpl(
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
            when (TokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE))) {
                TokenType.MULTI_TENANT_MAGIC_LINKS -> {
                    DeeplinkHandledStatus.Handled(
                        magicLinks.authenticate(B2BMagicLinks.AuthParameters(token, sessionDurationMinutes))
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
}

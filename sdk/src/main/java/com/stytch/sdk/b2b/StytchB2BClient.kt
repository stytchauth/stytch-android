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
 * The entrypoint for all Stytch B2B-related interaction.
 */
public object StytchB2BClient {
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal val sessionStorage = B2BSessionStorage(StorageHelper)
    internal var externalScope: CoroutineScope = GlobalScope // TODO: SDK-614

    /**
     * Configures the StytchB2BClient, setting the publicToken
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
     * Exposes an instance of email magic links
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
     * Exposes an instance of sessions
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
     * Exposes an instance of organization
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
     * Exposes an instance of member
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

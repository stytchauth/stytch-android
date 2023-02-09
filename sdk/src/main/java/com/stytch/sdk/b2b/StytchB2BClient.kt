package com.stytch.sdk.b2b

import android.content.Context
import android.os.Build
import com.stytch.sdk.b2b.magicLinks.MagicLinks
import com.stytch.sdk.b2b.magicLinks.MagicLinksImpl
import com.stytch.sdk.b2b.network.StytchB2BApi
import com.stytch.sdk.b2b.sessions.B2BSessionStorage
import com.stytch.sdk.b2b.sessions.Sessions
import com.stytch.sdk.b2b.sessions.SessionsImpl
import com.stytch.sdk.common.DeviceInfo
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.stytchError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

/**
 * The entrypoint for all Stytch B2B-related interaction.
 */
public object StytchB2BClient {
    internal var dispatchers: StytchDispatchers = StytchDispatchers()
    internal val sessionStorage = B2BSessionStorage(StorageHelper)
    internal var externalScope: CoroutineScope = GlobalScope // TODO: SDK-614

    /**
     * Configures the StytchB2BClient, setting the publicToken and hostUrl.
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @throws StytchExceptions.Critical - if failed to generate new encryption keys
     */
    public fun configure(context: Context, publicToken: String) {
        try {
            val deviceInfo = getDeviceInfo(context)
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
                "StytchClient not configured. You must call 'StytchClient.configure(...)' before using any functionality of the StytchClient." // ktlint-disable max-line-length
            )
        }
    }

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
     * Exposes an instance of email magic links
     */
    public var magicLinks: MagicLinks = MagicLinksImpl(
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
    public var sessions: Sessions = SessionsImpl(
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
}

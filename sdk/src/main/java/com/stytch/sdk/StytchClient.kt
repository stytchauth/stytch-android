package com.stytch.sdk

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.AuthData
import com.stytch.sdk.network.responseData.BasicData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

public typealias LoginOrCreateUserByEmailResponse = StytchResult<BasicData>
public typealias BaseResponse = StytchResult<BasicData>
public typealias AuthResponse = StytchResult<AuthData>

/**
 * The entrypoint for all Stytch-related interaction.
 */
public object StytchClient {

    internal lateinit var storageHelper: StorageHelper

    internal var ioDispatcher: CoroutineDispatcher = Dispatchers.IO
        private set
    internal var uiDispatcher: CoroutineDispatcher = Dispatchers.Main
        private set

    internal val sessionStorage = SessionStorage()

    /**
     * Configures the StytchClient, setting the publicToken and hostUrl.
     * @param publicToken Available via the Stytch dashboard in the API keys section
     * @param hostUrl This is an https url which will be used as the domain for setting session-token cookies to be sent to your servers on subsequent requests
     */
    public fun configure(context: Context, publicToken: String, hostUrl: String) {
        val deviceInfo = getDeviceInfo(context)
        StytchApi.configure(publicToken, hostUrl, deviceInfo)
        storageHelper = StorageHelper(context)
    }

    internal fun assertInitialized() {
        if (!StytchApi.isInitialized) {
            stytchError("StytchClient not configured. You must call 'StytchClient.configure(...)' before using any functionality of the StytchClient.")
        }
    }

    public var magicLinks: MagicLinks = MagicLinksImpl()
        private set
        get() {
            assertInitialized()
            return field
        }

    public var otps: OTP = OTPImpl()
        private set
        get() {
            assertInitialized()
            return field
        }

    public var sessions: Sessions = SessionsImpl()
        private set
        get() {
            assertInitialized()
            return field
        }

    /**
     * Set dispatchers for UI and IO tasks
     */
    public fun setDispatchers(uiDispatcher: CoroutineDispatcher, ioDispatcher: CoroutineDispatcher) {
        this.uiDispatcher = uiDispatcher
        this.ioDispatcher = ioDispatcher
    }

    //    TODO:("OTP")
    public object OneTimePasscodes {
//    fun loginOrCreate(parameters:completion:)
//    fun authenticate(parameters:completion:)
    }

//    TODO("OAuth")
//    TODO("User Management")

    private fun getDeviceInfo(context: Context): DeviceInfo {
        val deviceInfo = DeviceInfo()
        deviceInfo.applicationPackageName = context.applicationContext.packageName
        deviceInfo.osVersion = Build.VERSION.SDK_INT.toString()
        deviceInfo.deviceName = Build.MODEL
        deviceInfo.osName = Build.VERSION.CODENAME

        try {
//          throw exceptions if packageName not found
            deviceInfo.applicationVersion =
                context.applicationContext.packageManager.getPackageInfo(deviceInfo.applicationPackageName!!, 0).versionName
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
     */
    public suspend fun handle(uri: Uri, sessionDurationMinutes: UInt): AuthResponse {
        assertInitialized()
        val result: AuthResponse
        withContext(ioDispatcher) {
            val token = uri.getQueryParameter(Constants.QUERY_TOKEN)
            val tokenType = TokenType.fromString(uri.getQueryParameter(Constants.QUERY_TOKEN_TYPE))

            if (token.isNullOrEmpty())
                TODO("create a more graceful handling of bad parameters")

            when (tokenType) {
                TokenType.MAGIC_LINKS -> {
                    result = magicLinks.authenticate(MagicLinks.AuthParameters(token, sessionDurationMinutes))
                }
                TokenType.OAUTH -> {
                    TODO("Implement oauth handling")
                }
                TokenType.PASSWORD_RESET -> {
                    TODO("Implement password reset handling")
                }
                TokenType.UNKNOWN -> {
                    TODO("return Error")
                }
            }
        }

        return result
    }

    public fun handle(uri: Uri, sessionDurationMinutes: UInt, callback: (response: AuthResponse) -> Unit) {
        GlobalScope.launch(uiDispatcher) {
            val result = handle(uri, sessionDurationMinutes)
//              change to main thread to call callback
            callback(result)
        }
    }
}

internal object StytchLog {
    fun e(message: String) = Log.e("StytchLog", "Stytch error: $message")
    fun w(message: String) = Log.w("StytchLog", "Stytch warning: $message")
    fun i(message: String) = Log.i("StytchLog", message)
    fun d(message: String) = Log.d("StytchLog", message)
    fun v(message: String) = Log.v("StytchLog", message)
}

internal fun stytchError(message: String): Nothing {
    error("Stytch error: $message")
}
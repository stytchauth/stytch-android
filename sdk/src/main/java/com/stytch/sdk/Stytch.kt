package com.stytch.sdk

import android.net.Uri

public class Stytch private constructor() {
    internal var config: StytchConfig? = null

    private lateinit var flowManager: StytchFlowManager

    public var environment: StytchEnvironment = StytchEnvironment.LIVE

    public var loginMethod: StytchLoginMethod = StytchLoginMethod.LoginOrSignUp

    public var listener: StytchListener? = null

    public fun configure(projectID: String, secret: String, scheme: String, host: String) {
        config = StytchConfig(
            projectID = projectID,
            secret = secret,
            deeplinkScheme = scheme,
            deeplinkHost = host,
        )
        flowManager = StytchFlowManager()
    }

    public fun configure(projectID: String, secret: String, deeplink: Uri) {
        val scheme = deeplink.scheme ?: throw Exception("Provided deeplink Uri has a null scheme")
        val host = deeplink.host ?: throw Exception("Provided deeplink Uri has a null host")
        configure(projectID, secret, scheme, host)
    }

    public fun login(email: String) {
        checkIfConfigured()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            listener?.onFailure(StytchError.InvalidEmail)
            return
        }
        flowManager.login(email)
    }

    private fun resendEmailVerification() {
        checkIfConfigured()
        flowManager.resendEmailVerification()
    }

    public fun handleDeepLink(uri: Uri): Boolean {
        checkIfConfigured()
        if (uri.scheme == config?.deeplinkScheme && uri.host == config?.deeplinkHost) {
            uri.path?.let { path ->
//                TODO: handle different deep link paths
                when {
                    path.contains(Constants.LOGIN_PATH) -> {
                        flowManager.verifyToken(uri.getQueryParameter("token"))
                        return true
                    }
                    path.contains(Constants.INVITE_PATH) -> {
                        flowManager.verifyToken(uri.getQueryParameter("token"))
                        return true
                    }
                    path.contains(Constants.SIGN_UP_PATH) -> {
                        flowManager.verifyToken(uri.getQueryParameter("token"))
                        return true
                    }
                    else -> {
                        return false
                    }
                }
            }
            return false
        }
        return false
    }

    private fun checkIfConfigured() {
        if (config == null) {
            throw UninitializedPropertyAccessException(Constants.NOT_INITIALIZED_WARNING)
        }
    }

    public companion object {
        public val instance: Stytch = Stytch()
    }

    public interface StytchListener {
        public fun onSuccess(result: StytchResult)
        public fun onFailure(error: StytchError)
        public fun onMagicLinkSent(email: String)
    }

}

internal class StytchConfig(
    val projectID: String = "",
    val secret: String = "",
    val deeplinkScheme: String = "https",
    val deeplinkHost: String = "test.stytch.com",
    val verifyEmail: Boolean = false,
    var uiCustomization: StytchUICustomization = StytchUICustomization(),
)

public enum class StytchEnvironment {
    LIVE,
    TEST,
}

public enum class StytchError(public val messageId: Int) {
    InvalidEmail(R.string.stytch_error_invalid_input),
    Connection(R.string.stytch_error_no_internet),
    Unknown(R.string.stytch_error_unknown),
    InvalidMagicToken(R.string.stytch_error_invalid_magic_token),
    InvalidConfiguration(R.string.stytch_error_bad_token),
}

public class StytchEvent private constructor(
    public val type: String,
    public val created: Boolean,
    public val userId: String,
) {

    public companion object {
        private const val USER_EVENT = "user_event"

        public fun userCreatedEvent(userId: String): StytchEvent {
            return StytchEvent(USER_EVENT, true, userId)
        }

        public fun userFoundEvent(userId: String): StytchEvent {
            return StytchEvent(USER_EVENT, false, userId)
        }
    }
}

public enum class StytchLoginMethod {
    LoginOrSignUp,
    LoginOrInvite,
}

public data class StytchResult(
    public val userId: String,
    public val requestId: String,
) {
    override fun toString(): String {
        return "StytchResult(userId='$userId', requestId='$requestId')"
    }
}

package com.stytch.sdk

import android.net.Uri
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.Constants
import com.stytch.sdk.helpers.StytchFlowManager

public class Stytch private constructor() {
    internal var config: StytchConfig? = null

    private lateinit var flowManager: StytchFlowManager

    public var environment = StytchEnvironment.LIVE

    public var loginMethod = StytchLoginMethod.LoginOrSignUp

    public var listener: StytchListener? = null

    public fun configure(projectID: String, secret: String, scheme: String, host: String) {
        config = StytchConfig.Builder()
            .withAuth(projectID, secret)
            .withDeepLinkScheme(scheme)
            .withDeepLinkHost(host)
            .build()
        flowManager = StytchFlowManager()
    }
	
	public fun configure(projectID: String, secret: String, universalLink: Uri) {
		config = StytchConfig.Builder()
				.withAuth(projectID, secret)
				.withUniversalLink(universalLink)
				.build()
		flowManager = StytchFlowManager()
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
        if (uri.scheme == config?.deepLinkScheme) {
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
        public val instance = Stytch()
    }

    public interface StytchListener {
        public fun onSuccess(result: StytchResult)
        public fun onFailure(error: StytchError)
        public fun onMagicLinkSent(email: String)
    }

}
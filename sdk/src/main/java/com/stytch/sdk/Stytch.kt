package com.stytch.sdk

import android.net.Uri
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.Constants
import com.stytch.sdk.helpers.StytchFlowManager

public class Stytch private constructor() {
    internal lateinit var config: StytchConfig

    private lateinit var flowManager: StytchFlowManager

    public var listener: StytchListener? = null

    public fun configure(projectID: String, secret: String, scheme: String) {
        config = StytchConfig.Builder()
            .withAuth(projectID, secret)
            .withDeepLinkScheme(scheme)
            .withDeepLinkHost(Constants.HOST)
            .build()
        flowManager = StytchFlowManager()
    }

    public fun login(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            listener?.onFailure(StytchError.InvalidEmail)
            return
        }
        flowManager.login(email)
    }

    private fun resendEmailVerification() {
        flowManager.resendEmailVerification()
    }

    public fun handleDeepLink(uri: Uri): Boolean {
        if (uri.scheme == config.deepLinkScheme) {
            flowManager.verifyToken(uri.getQueryParameter("token"))
            return true
        }
        return false
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
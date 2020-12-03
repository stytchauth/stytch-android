package com.stytch.sdk

import android.net.Uri
import com.stytch.sdk.api.StytchResult
import com.stytch.sdk.helpers.Constants
import com.stytch.sdk.helpers.StytchFlowManager

class Stytch private constructor() {
    internal lateinit var config: StytchConfig

    private lateinit var flowManager: StytchFlowManager

    var listener: StytchListener? = null

    fun configure(projectID: String, secret: String, scheme: String) {
        config = StytchConfig.Builder()
            .withAuth(projectID, secret)
            .withDeepLinkScheme(scheme)
            .withDeepLinkHost(Constants.HOST)
            .build()
        flowManager = StytchFlowManager()
    }

    fun login(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            listener?.onFailure(StytchError.InvalidEmail)
            return
        }
        flowManager.login(email)
    }

    private fun resendEmailVerification() {
        flowManager.resendEmailVerification()
    }

    fun handleDeepLink(uri: Uri): Boolean {
        if (uri.scheme == config.deepLinkScheme) {
            flowManager.verifyToken(uri.getQueryParameter("token"))
            return true
        }
        return false
    }


    companion object {
        val instance = Stytch()
    }

    interface StytchListener {

        fun onSuccess(result: StytchResult)
        fun onFailure(error: StytchError)
        fun onMagicLinkSent(email: String)
    }

}
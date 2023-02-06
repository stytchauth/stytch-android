package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.network.StytchErrorType
import com.stytch.sdk.consumer.network.SessionData

internal class B2BSessionStorage(private val storageHelper: StorageHelper) {
    var sessionToken: String?
        private set(value) {
            storageHelper.saveValue(Constants.PREFERENCES_NAME_SESSION_TOKEN, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = storageHelper.loadValue(Constants.PREFERENCES_NAME_SESSION_TOKEN)
            }
            return value
        }

    var sessionJwt: String?
        private set(value) {
            storageHelper.saveValue(Constants.PREFERENCES_NAME_SESSION_JWT, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = storageHelper.loadValue(Constants.PREFERENCES_NAME_SESSION_JWT)
            }
            return value
        }

    /**
     * @throws Exception if failed to save data
     */
    fun updateSession(sessionToken: String?, sessionJwt: String?, session: SessionData? = null) {
        synchronized(this) {
            this.sessionToken = sessionToken
            this.sessionJwt = sessionJwt
            // this.session = session
        }
    }

    /**
     * @throws Exception if failed to save data
     */
    fun revoke() {
        synchronized(this) {
            sessionToken = null
            sessionJwt = null
            // session = null
            // user = null
        }
    }

    fun ensureSessionIsValidOrThrow() {
        if (sessionToken == null && sessionJwt == null) {
            throw StytchExceptions.Input(StytchErrorType.NO_CURRENT_SESSION.message)
        }
    }
}

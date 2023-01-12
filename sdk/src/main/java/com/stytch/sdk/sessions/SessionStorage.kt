package com.stytch.sdk.sessions

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.StytchExceptions
import com.stytch.sdk.network.StytchErrorType
import com.stytch.sdk.network.responseData.SessionData
import com.stytch.sdk.network.responseData.UserData

@VisibleForTesting
internal const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"

@VisibleForTesting
internal const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"

internal class SessionStorage(private val storageHelper: StorageHelper) {
    var sessionToken: String?
        private set(value) {
            storageHelper.saveValue(PREFERENCES_NAME_SESSION_TOKEN, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = storageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN)
            }
            return value
        }

    var sessionJwt: String?
        private set(value) {
            storageHelper.saveValue(PREFERENCES_NAME_SESSION_JWT, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = storageHelper.loadValue(PREFERENCES_NAME_SESSION_JWT)
            }
            return value
        }

    var session: SessionData? = null
        private set

    var user: UserData? = null
        set(value) {
            synchronized(this) {
                field = value
            }
        }
        get() {
            synchronized(this) {
                return field
            }
        }

    /**
     * @throws Exception if failed to save data
     */
    fun updateSession(sessionToken: String?, sessionJwt: String?, session: SessionData? = null) {
        synchronized(this) {
            this.sessionToken = sessionToken
            this.sessionJwt = sessionJwt
            this.session = session
        }
    }

    /**
     * @throws Exception if failed to save data
     */
    fun revoke() {
        synchronized(this) {
            sessionToken = null
            sessionJwt = null
            session = null
            user = null
        }
    }

    fun ensureSessionIsValidOrThrow() {
        if (sessionToken == null && sessionJwt == null) {
            throw StytchExceptions.Input(StytchErrorType.NO_CURRENT_SESSION.message)
        }
    }
}

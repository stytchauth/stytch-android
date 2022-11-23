package com.stytch.sessions

import androidx.annotation.VisibleForTesting
import com.stytch.sdk.StorageHelper
import com.stytch.sdk.network.responseData.SessionData

@VisibleForTesting
internal const val PREFERENCES_NAME_SESSION_JWT = "session_jwt"

@VisibleForTesting
internal const val PREFERENCES_NAME_SESSION_TOKEN = "session_token"

private const val COOKIE_NAME_SESSION_JWT = "stytch_session_jwt"
private const val COOKIE_NAME_SESSION_TOKEN = "stytch_session"

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
        private set(value) {
            field = value
        }

    val cookies: Map<String?, String?>?
        get() {
            return storageHelper.getAllCookies(
                mapOf(
                    COOKIE_NAME_SESSION_JWT to PREFERENCES_NAME_SESSION_JWT,
                    COOKIE_NAME_SESSION_TOKEN to PREFERENCES_NAME_SESSION_TOKEN
                )
            )
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
        }
    }
}

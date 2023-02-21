package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.network.B2BSessionData
import com.stytch.sdk.b2b.network.MemberData
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.sessions.SessionStorage

internal class B2BSessionStorage(private val storageHelper: StorageHelper) : SessionStorage {
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

    var memberSession: B2BSessionData? = null
        private set

    var member: MemberData? = null
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

    val activeSessionExists: Boolean
        get() = sessionToken != null || sessionJwt != null

    /**
     * @throws Exception if failed to save data
     */
    fun updateSession(sessionToken: String?, sessionJwt: String?, session: B2BSessionData? = null) {
        synchronized(this) {
            this.sessionToken = sessionToken
            this.sessionJwt = sessionJwt
            this.memberSession = session
        }
    }

    /**
     * @throws Exception if failed to save data
     */
    fun revoke() {
        synchronized(this) {
            sessionToken = null
            sessionJwt = null
            memberSession = null
            member = null
        }
    }
}

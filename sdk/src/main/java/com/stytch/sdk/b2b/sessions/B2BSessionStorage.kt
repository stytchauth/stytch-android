package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper

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
            return if (value?.isEmpty() == true) null else value
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
    var intermediateSessionToken: String?
        private set(value) {
            storageHelper.saveValue(Constants.PREFERENCES_NAME_IST, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = storageHelper.loadValue(Constants.PREFERENCES_NAME_IST)
            }
            return value
        }

    var memberSession: B2BSessionData? = null
        internal set

    var member: MemberData? = null
        internal set

    var organization: OrganizationData? = null
        internal set

    val persistedSessionIdentifiersExist: Boolean
        get() = sessionToken != null || sessionJwt != null

    /**
     * @throws Exception if failed to save data
     */
    fun updateSession(
        sessionToken: String?,
        sessionJwt: String?,
        session: B2BSessionData? = null,
    ) {
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

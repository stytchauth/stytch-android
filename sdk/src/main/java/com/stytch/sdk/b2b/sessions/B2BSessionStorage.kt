package com.stytch.sdk.b2b.sessions

import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class B2BSessionStorage(
    private val storageHelper: StorageHelper,
    private val externalScope: CoroutineScope,
) {
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
        internal set(value) {
            field = value
            externalScope.launch {
                _sessionFlow.emit(field)
            }
        }

    var member: MemberData? = null
        internal set(value) {
            field = value
            externalScope.launch {
                _memberFlow.emit(field)
            }
        }

    var organization: OrganizationData? = null
        internal set(value) {
            field = value
            externalScope.launch {
                _organizationFlow.emit(field)
            }
        }

    val persistedSessionIdentifiersExist: Boolean
        get() = sessionToken != null || sessionJwt != null

    private val _sessionFlow = MutableStateFlow(memberSession)
    val sessionFlow = _sessionFlow.asStateFlow()

    private val _memberFlow = MutableStateFlow(member)
    val memberFlow = _memberFlow.asStateFlow()

    private val _organizationFlow = MutableStateFlow(organization)
    val organizationFlow = _organizationFlow.asStateFlow()

    /**
     * @throws Exception if failed to save data
     */
    fun updateSession(
        sessionToken: String?,
        sessionJwt: String?,
        session: B2BSessionData? = null,
        intermediateSessionToken: String? = null,
    ) {
        synchronized(this) {
            this.sessionToken = sessionToken
            this.sessionJwt = sessionJwt
            this.memberSession = session
            this.intermediateSessionToken = intermediateSessionToken
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
            intermediateSessionToken = null
        }
    }
}

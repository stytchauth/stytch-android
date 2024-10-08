package com.stytch.sdk.b2b.sessions

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.stytch.sdk.b2b.network.models.B2BSessionData
import com.stytch.sdk.b2b.network.models.MemberData
import com.stytch.sdk.b2b.network.models.OrganizationData
import com.stytch.sdk.common.IST_EXPIRATION_TIME
import com.stytch.sdk.common.PREFERENCES_NAME_IST
import com.stytch.sdk.common.PREFERENCES_NAME_IST_EXPIRATION
import com.stytch.sdk.common.PREFERENCES_NAME_LAST_VALIDATED_AT
import com.stytch.sdk.common.PREFERENCES_NAME_MEMBER_DATA
import com.stytch.sdk.common.PREFERENCES_NAME_MEMBER_SESSION_DATA
import com.stytch.sdk.common.PREFERENCES_NAME_ORGANIZATION_DATA
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_JWT
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_TOKEN
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.utils.getDateOrMin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

internal class B2BSessionStorage(
    private val storageHelper: StorageHelper,
    private val externalScope: CoroutineScope,
) {
    private val moshi = Moshi.Builder().build()
    private val moshiB2BSessionDataAdapter = moshi.adapter(B2BSessionData::class.java).lenient()
    private val moshiMemberDataAdapter = moshi.adapter(MemberData::class.java).lenient()
    private val moshiOrganizationDataAdapter = moshi.adapter(OrganizationData::class.java).lenient()

    var sessionToken: String?
        private set(value) {
            storageHelper.saveValue(PREFERENCES_NAME_SESSION_TOKEN, value)
        }
        get() {
            val value: String?
            synchronized(this) {
                value = storageHelper.loadValue(PREFERENCES_NAME_SESSION_TOKEN)
            }
            return if (value?.isEmpty() == true) null else value
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

    var intermediateSessionToken: String?
        private set(value) {
            storageHelper.saveValue(PREFERENCES_NAME_IST, value)
            // Set the IST expiration when persisting an IST
            intermediateSessionTokenExpiration =
                if (value != null) {
                    Date().time + IST_EXPIRATION_TIME
                } else {
                    0L
                }
        }
        get() {
            val value: String?
            synchronized(this) {
                // Only load and return an IST if it isn't expired
                value =
                    if (intermediateSessionTokenExpiration >= Date().time) {
                        storageHelper.loadValue(PREFERENCES_NAME_IST)
                    } else {
                        // if IST is expired, null it out (this will also null the expiration in the setter)
                        intermediateSessionToken = null
                        null
                    }
            }
            return value
        }

    private var intermediateSessionTokenExpiration: Long
        set(value) {
            storageHelper.saveLong(PREFERENCES_NAME_IST_EXPIRATION, value)
        }
        get() {
            val value: Long
            synchronized(this) {
                value = storageHelper.getLong(PREFERENCES_NAME_IST_EXPIRATION)
            }
            return value
        }

    var lastValidatedAt: Date
        get() {
            val longValue: Long?
            synchronized(this) {
                longValue = storageHelper.getLong(PREFERENCES_NAME_LAST_VALIDATED_AT)
            }
            return longValue?.let { Date(it) } ?: Date(0L)
        }
        private set(value) {
            storageHelper.saveLong(PREFERENCES_NAME_LAST_VALIDATED_AT, value.time)
            externalScope.launch {
                _lastValidatedAtFlow.emit(value)
            }
        }

    var memberSession: B2BSessionData?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_MEMBER_SESSION_DATA)
            }
            return stringValue?.let {
                // convert it back to a data class, check the expiration date, expire it if expired
                val memberSessionData =
                    try {
                        moshiB2BSessionDataAdapter.fromJson(it)
                    } catch (e: JsonDataException) {
                        StytchLog.e(e.message ?: "Error parsing persisted B2BSessionData")
                        null
                    }
                val expirationDate = memberSessionData?.expiresAt.getDateOrMin()
                val now = Date()
                if (expirationDate.before(now)) {
                    revoke()
                    return null
                }
                return memberSessionData
            }
        }
        private set(value) {
            value?.let {
                val stringValue = moshiB2BSessionDataAdapter.toJson(it)
                storageHelper.saveValue(PREFERENCES_NAME_MEMBER_SESSION_DATA, stringValue)
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_MEMBER_SESSION_DATA, null)
            }
            lastValidatedAt = Date()
            externalScope.launch {
                _sessionFlow.emit(value)
            }
        }

    var member: MemberData?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_MEMBER_DATA)
            }
            return stringValue?.let {
                try {
                    moshiMemberDataAdapter.fromJson(it)
                } catch (e: JsonDataException) {
                    StytchLog.e(e.message ?: "Error parsing persisted MemberData")
                    null
                }
            }
        }
        internal set(value) {
            value?.let {
                val stringValue = moshiMemberDataAdapter.toJson(it)
                storageHelper.saveValue(PREFERENCES_NAME_MEMBER_DATA, stringValue)
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_MEMBER_DATA, null)
            }
            lastValidatedAt = Date()
            externalScope.launch {
                _memberFlow.emit(value)
            }
        }

    var organization: OrganizationData?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_ORGANIZATION_DATA)
            }
            return stringValue?.let {
                try {
                    moshiOrganizationDataAdapter.fromJson(it)
                } catch (e: JsonDataException) {
                    StytchLog.e(e.message ?: "Error parsing persisted OrganizationData")
                    null
                }
            }
        }
        internal set(value) {
            value?.let {
                val stringValue = moshiOrganizationDataAdapter.toJson(it)
                storageHelper.saveValue(PREFERENCES_NAME_ORGANIZATION_DATA, stringValue)
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_ORGANIZATION_DATA, null)
            }
            lastValidatedAt = Date()
            externalScope.launch {
                _organizationFlow.emit(value)
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

    private val _lastValidatedAtFlow = MutableStateFlow(lastValidatedAt)
    val lastValidatedAtFlow = _lastValidatedAtFlow.asStateFlow()

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
            lastValidatedAt = Date(0L)
        }
    }
}

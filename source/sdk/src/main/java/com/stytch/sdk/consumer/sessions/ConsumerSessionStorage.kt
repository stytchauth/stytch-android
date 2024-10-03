package com.stytch.sdk.consumer.sessions

import com.squareup.moshi.Moshi
import com.stytch.sdk.common.PREFERENCES_NAME_LAST_VALIDATED_AT
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_DATA
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_JWT
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_TOKEN
import com.stytch.sdk.common.PREFERENCES_NAME_USER_DATA
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.errors.StytchNoCurrentSessionError
import com.stytch.sdk.consumer.extensions.keepLocalBiometricRegistrationsInSync
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

internal class ConsumerSessionStorage(
    private val storageHelper: StorageHelper,
    private val externalScope: CoroutineScope,
) {
    private val moshi =
        Moshi
            .Builder()
            .add(SessionData::class.java)
            .add(UserData::class.java)
            .build()

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

    var lastValidatedAt: Date
        get() {
            val longValue: Long?
            synchronized(this) {
                longValue = storageHelper.getLong(PREFERENCES_NAME_LAST_VALIDATED_AT)
            }
            return longValue?.let {
                Date.from(Instant.ofEpochMilli(it))
            } ?: Date.from(Instant.MIN)
        }
        set(value) {
            storageHelper.saveLong(PREFERENCES_NAME_LAST_VALIDATED_AT, value.time)
        }

    var session: SessionData?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_SESSION_DATA)
            }
            return stringValue?.let {
                moshi.adapter(SessionData::class.java).fromJson(it)
            }
        }
        private set(value) {
            value?.let {
                val stringValue = moshi.adapter(SessionData::class.java).toJson(it)
                storageHelper.saveValue(PREFERENCES_NAME_SESSION_DATA, stringValue)
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_SESSION_DATA, null)
            }
            lastValidatedAt = Date()
            externalScope.launch {
                _sessionFlow.emit(value)
            }
        }

    var user: UserData?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_USER_DATA)
            }
            return stringValue?.let {
                moshi.adapter(UserData::class.java).fromJson(it)
            }
        }
        internal set(value) {
            value?.let {
                it.keepLocalBiometricRegistrationsInSync(storageHelper)
                val stringValue = moshi.adapter(UserData::class.java).toJson(it)
                storageHelper.saveValue(PREFERENCES_NAME_USER_DATA, stringValue)
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_USER_DATA, null)
            }
            lastValidatedAt = Date()
            externalScope.launch {
                _userFlow.emit(value)
            }
        }

    private val _sessionFlow = MutableStateFlow(session)
    val sessionFlow = _sessionFlow.asStateFlow()

    private val _userFlow = MutableStateFlow(user)
    val userFlow = _userFlow.asStateFlow()

    val persistedSessionIdentifiersExist: Boolean
        get() = sessionToken != null || sessionJwt != null

    var methodId: String? = null

    /**
     * @throws Exception if failed to save data
     */
    fun updateSession(
        sessionToken: String?,
        sessionJwt: String?,
        session: SessionData? = null,
    ) {
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
            throw StytchNoCurrentSessionError()
        }
    }
}

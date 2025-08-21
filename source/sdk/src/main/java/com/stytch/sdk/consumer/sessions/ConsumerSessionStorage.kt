package com.stytch.sdk.consumer.sessions

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.stytch.sdk.common.PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID
import com.stytch.sdk.common.PREFERENCES_NAME_LAST_AUTH_METHOD_USED
import com.stytch.sdk.common.PREFERENCES_NAME_LAST_VALIDATED_AT
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_DATA
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_JWT
import com.stytch.sdk.common.PREFERENCES_NAME_SESSION_TOKEN
import com.stytch.sdk.common.PREFERENCES_NAME_USER_DATA
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.StytchLog
import com.stytch.sdk.common.errors.StytchNoCurrentSessionError
import com.stytch.sdk.common.utils.SHORT_FORM_DATE_FORMATTER
import com.stytch.sdk.common.utils.getDateOrMin
import com.stytch.sdk.consumer.ConsumerAuthMethod
import com.stytch.sdk.consumer.biometrics.LAST_USED_BIOMETRIC_REGISTRATION_ID
import com.stytch.sdk.consumer.extensions.keepLocalBiometricRegistrationsInSync
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import java.util.TimeZone

internal class ConsumerSessionStorage(
    private val storageHelper: StorageHelper,
) {
    private val moshi = Moshi.Builder().build()
    private val moshiSessionDataAdapter = moshi.adapter(SessionData::class.java).lenient()
    private val moshiUserDataAdapter = moshi.adapter(UserData::class.java).lenient()
    private val moshiLastAuthMethodUsedAdapter = moshi.adapter(ConsumerAuthMethod::class.java).lenient()

    private val _sessionFlow = MutableStateFlow<SessionData?>(null)
    private val _userFlow = MutableStateFlow<UserData?>(null)
    private val _lastValidatedAtFlow = MutableStateFlow<Date>(Date(0L))

    val sessionFlow = _sessionFlow.asStateFlow()
    val userFlow = _userFlow.asStateFlow()
    val lastValidatedAtFlow = _lastValidatedAtFlow.asStateFlow()

    private var lastAuthenticatedUserId: String?
        get() {
            synchronized(this) {
                return storageHelper.loadValue(PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID)
            }
        }
        set(value) {
            // we're only ever setting this to a string, never deleting it
            storageHelper.saveValue(PREFERENCES_NAME_LAST_AUTHENTICATED_USER_ID, value)
        }

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
            return longValue?.let { Date(it) } ?: Date(0L)
        }
        private set(value) {
            storageHelper.saveLong(PREFERENCES_NAME_LAST_VALIDATED_AT, value.time)
            _lastValidatedAtFlow.tryEmit(value)
        }

    var session: SessionData?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_SESSION_DATA)
            }
            return stringValue?.let {
                // convert it back to a data class, check the expiration date, expire it if expired
                val sessionData =
                    try {
                        moshiSessionDataAdapter.fromJson(it)
                    } catch (e: JsonDataException) {
                        StytchLog.e(e.message ?: "Error parsing persisted SessionData")
                        null
                    }
                val expirationDate = sessionData?.expiresAt.getDateOrMin()
                val formatter = SHORT_FORM_DATE_FORMATTER
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val now = formatter.format(Date()).getDateOrMin()
                if (expirationDate.before(now)) {
                    revoke()
                    return null
                }
                return sessionData
            }
        }
        private set(value) {
            value?.let {
                val stringValue = moshiSessionDataAdapter.toJson(it)
                storageHelper.saveValue(PREFERENCES_NAME_SESSION_DATA, stringValue)
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_SESSION_DATA, null)
            }
            lastValidatedAt = Date()
            _sessionFlow.tryEmit(value)
        }

    var user: UserData?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_USER_DATA)
            }
            return stringValue?.let {
                try {
                    moshiUserDataAdapter.fromJson(it)
                } catch (e: JsonDataException) {
                    StytchLog.e(e.message ?: "Error parsing persisted UserData")
                    null
                }
            }
        }
        internal set(value) {
            value?.let { currentUser ->
                // first, save the current user
                val stringValue = moshiUserDataAdapter.toJson(currentUser)
                storageHelper.saveValue(PREFERENCES_NAME_USER_DATA, stringValue)
                // then, perform any necessary cleanup
                lastAuthenticatedUserId?.let { previousUserId ->
                    // if we have a record of a previous user, process/clean up local biometric registrations as needed
                    processPotentialBiometricRegistrationCleanups(currentUser, previousUserId)
                } ?: run {
                    // if we have no previous user, just clean up any local registrations that don't exist on the server
                    currentUser.keepLocalBiometricRegistrationsInSync(storageHelper)
                }
                // update lastAuthenticatedUserId with new user id
                lastAuthenticatedUserId = currentUser.userId
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_USER_DATA, null)
            }
            lastValidatedAt = Date()
            _userFlow.tryEmit(value)
        }

    internal var lastAuthMethodUsed: ConsumerAuthMethod?
        get() {
            val stringValue: String?
            synchronized(this) {
                stringValue = storageHelper.loadValue(PREFERENCES_NAME_LAST_AUTH_METHOD_USED)
            }
            return stringValue?.let {
                try {
                    moshiLastAuthMethodUsedAdapter.fromJson(it)
                } catch (e: JsonDataException) {
                    StytchLog.e(e.message ?: "Error parsing persisted last auth method")
                    null
                }
            }
        }
        set(value) {
            value?.let {
                val stringValue = moshiLastAuthMethodUsedAdapter.toJson(it)
                storageHelper.saveValue(PREFERENCES_NAME_LAST_AUTH_METHOD_USED, stringValue)
            } ?: run {
                storageHelper.saveValue(PREFERENCES_NAME_LAST_AUTH_METHOD_USED, null)
            }
        }

    val persistedSessionIdentifiersExist: Boolean
        get() = sessionToken != null || sessionJwt != null

    var methodId: String? = null

    fun emitCurrent() {
        _sessionFlow.tryEmit(session)
        _userFlow.tryEmit(user)
        _lastValidatedAtFlow.tryEmit(lastValidatedAt)
    }

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
            lastValidatedAt = Date(0L)
        }
    }

    fun ensureSessionIsValidOrThrow() {
        if (sessionToken == null && sessionJwt == null) {
            throw StytchNoCurrentSessionError()
        }
    }

    private fun processPotentialBiometricRegistrationCleanups(
        currentUser: UserData,
        previousUserId: String,
    ) {
        // if the previous and current user are the same
        if (previousUserId == currentUser.userId) {
            // only clean up any local registrations that don't exist on the server
            currentUser.keepLocalBiometricRegistrationsInSync(storageHelper)
        } else {
            // if there is an existing biometric registration on the device, delete the local registration to enable the
            // new user to create their own biometric registration
            storageHelper.loadValue(LAST_USED_BIOMETRIC_REGISTRATION_ID)?.let { existingBiometricRegistrationId ->
                storageHelper.deleteAllBiometricsKeys()
            }
        }
    }
}

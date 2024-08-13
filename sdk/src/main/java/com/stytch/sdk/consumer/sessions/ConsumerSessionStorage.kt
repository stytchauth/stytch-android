package com.stytch.sdk.consumer.sessions

import com.stytch.sdk.common.Constants
import com.stytch.sdk.common.StorageHelper
import com.stytch.sdk.common.errors.StytchNoCurrentSessionError
import com.stytch.sdk.common.extensions.keepLocalBiometricRegistrationsInSync
import com.stytch.sdk.consumer.network.models.SessionData
import com.stytch.sdk.consumer.network.models.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class ConsumerSessionStorage(
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

    var session: SessionData? = null
        private set(value) {
            field = value
            externalScope.launch {
                _sessionFlow.emit(field)
            }
        }

    var user: UserData? = null
        set(value) {
            synchronized(this) {
                field = value
            }
            externalScope.launch {
                value?.keepLocalBiometricRegistrationsInSync(storageHelper)
                _userFlow.emit(value)
            }
        }
        get() {
            synchronized(this) {
                return field
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

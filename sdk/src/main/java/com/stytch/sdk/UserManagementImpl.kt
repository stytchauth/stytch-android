package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.UserData
import com.stytch.sessions.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class UserManagementImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val api: StytchApi.UserManagement,
) : UserManagement {
    override suspend fun getUser(): UserResponse {
        return catchExceptions {
            withContext(dispatchers.io) {
                api.getUser()
            }
        }

    }

    override fun getUser(callback: (UserResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = getUser()
            callback(result)
        }
    }

    override fun getSyncUser(): UserData? = sessionStorage.user

    override suspend fun deleteEmailById(id: String): BaseResponse {
        return catchExceptions {
            withContext(dispatchers.io) {
                api.deleteEmailById(id)
            }
        }
    }

    override fun deleteEmailById(id: String, callback: (BaseResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = deleteEmailById(id)
            callback(result)
        }
    }

    override suspend fun deletePhoneNumberById(id: String): BaseResponse {
        return catchExceptions {
            withContext(dispatchers.io) {
                api.deletePhoneNumberById(id)
            }
        }
    }

    override fun deletePhoneNumberById(id: String, callback: (BaseResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = deletePhoneNumberById(id)
            callback(result)
        }
    }

    override suspend fun deleteBiometricRegistrationById(id: String): BaseResponse {
        return catchExceptions {
            withContext(dispatchers.io) {
                api.deleteBiometricRegistrationById(id)
            }
        }
    }

    override fun deleteBiometricRegistrationById(id: String, callback: (BaseResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = deleteBiometricRegistrationById(id)
            callback(result)
        }
    }

    private suspend fun <T> catchExceptions(block: suspend () -> StytchResult<T>): StytchResult<T> = try {
        block()
    } catch (stytchException: StytchExceptions) {
        StytchResult.Error(stytchException)
    } catch (otherException: Exception) {
        StytchResult.Error(StytchExceptions.Critical(otherException))
    }
}
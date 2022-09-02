package com.stytch.sdk

import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.UserData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class UserManagementImpl : UserManagement {
    override suspend fun getUser(): UserResponse {
        return catchExceptions {
            val result: UserResponse

            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.UserManagement.getUser()
            }

            result
        }

    }

    override fun getUser(callback: (UserResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = getUser()
            callback(result)
        }
    }

    override fun getSyncUser(): UserData? {
        return StytchClient.sessionStorage.user
    }

    override suspend fun deleteEmailById(id: String): BaseResponse {
        return catchExceptions {
            val result: BaseResponse

            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.UserManagement.deleteEmailById(id)
            }

            result
        }
    }

    override fun deleteEmailById(id: String, callback: (BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = deleteEmailById(id)
            callback(result)
        }
    }

    override suspend fun deletePhoneNumberById(id: String): BaseResponse {
        return catchExceptions {
            val result: BaseResponse

            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.UserManagement.deletePhoneNumberById(id)
            }

            result
        }
    }

    override fun deletePhoneNumberById(id: String, callback: (BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = deletePhoneNumberById(id)
            callback(result)
        }
    }

    override suspend fun deleteBiometricRegistrationById(id: String): BaseResponse {
        return catchExceptions {
            val result: BaseResponse

            withContext(StytchClient.ioDispatcher) {
                result = StytchApi.UserManagement.deleteBiometricRegistrationById(id)
            }

            result
        }
    }

    override fun deleteBiometricRegistrationById(id: String, callback: (BaseResponse) -> Unit) {
        GlobalScope.launch(StytchClient.uiDispatcher) {
            val result = deleteBiometricRegistrationById(id)
            callback(result)
        }
    }

    private suspend fun <StytchResultType> catchExceptions(function: suspend () -> StytchResult<StytchResultType>): StytchResult<StytchResultType> {
        return try {
            function()
        } catch (stytchException: StytchExceptions) {
            when (stytchException) {
                StytchExceptions.NoCodeChallengeFound ->
                    StytchResult.Error(1, null)
            }
        } catch (otherException: Exception) {
            StytchResult.Error(1, null)
        }
    }
}
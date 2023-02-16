package com.stytch.sdk.consumer.userManagement

import com.stytch.sdk.common.StytchDispatchers
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.DeleteFactorResponse
import com.stytch.sdk.consumer.UserResponse
import com.stytch.sdk.consumer.network.StytchApi
import com.stytch.sdk.consumer.network.UserData
import com.stytch.sdk.consumer.sessions.SessionStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class UserManagementImpl(
    private val externalScope: CoroutineScope,
    private val dispatchers: StytchDispatchers,
    private val sessionStorage: SessionStorage,
    private val api: StytchApi.UserManagement,
) : UserManagement {
    override suspend fun getUser(): UserResponse = withContext(dispatchers.io) {
        api.getUser()
    }

    override fun getUser(callback: (UserResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = getUser()
            callback(result)
        }
    }

    override fun getSyncUser(): UserData? = sessionStorage.user

    override suspend fun deleteFactor(factor: UserAuthenticationFactor): DeleteFactorResponse =
        withContext(dispatchers.io) {
            when (factor) {
                is UserAuthenticationFactor.Email -> api.deleteEmailById(factor.id)
                is UserAuthenticationFactor.PhoneNumber -> api.deletePhoneNumberById(factor.id)
                is UserAuthenticationFactor.BiometricRegistration -> api.deleteBiometricRegistrationById(factor.id)
                is UserAuthenticationFactor.CryptoWallet -> api.deleteCryptoWalletById(factor.id)
                is UserAuthenticationFactor.WebAuthn -> api.deleteWebAuthnById(factor.id)
            }.apply {
                if (this is StytchResult.Success) {
                    sessionStorage.user = this.value.user
                }
            }
        }

    override fun deleteFactor(factor: UserAuthenticationFactor, callback: (DeleteFactorResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = deleteFactor(factor)
            callback(result)
        }
    }
}

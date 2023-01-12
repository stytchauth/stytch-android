package com.stytch.sdk.userManagement

import com.stytch.sdk.DeleteFactorResponse
import com.stytch.sdk.StytchDispatchers
import com.stytch.sdk.StytchResult
import com.stytch.sdk.UserResponse
import com.stytch.sdk.network.StytchApi
import com.stytch.sdk.network.responseData.UserData
import com.stytch.sdk.sessions.SessionStorage
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

    override suspend fun deleteFactor(factor: AuthenticationFactor): DeleteFactorResponse =
        withContext(dispatchers.io) {
            when (factor) {
                is AuthenticationFactor.Email -> api.deleteEmailById(factor.id)
                is AuthenticationFactor.PhoneNumber -> api.deletePhoneNumberById(factor.id)
                is AuthenticationFactor.BiometricRegistration -> api.deleteBiometricRegistrationById(factor.id)
                is AuthenticationFactor.CryptoWallet -> api.deleteCryptoWalletById(factor.id)
                is AuthenticationFactor.WebAuthn -> api.deleteWebAuthnById(factor.id)
            }.apply {
                if (this is StytchResult.Success) {
                    sessionStorage.user = this.value.user
                }
            }
        }

    override fun deleteFactor(factor: AuthenticationFactor, callback: (DeleteFactorResponse) -> Unit) {
        externalScope.launch(dispatchers.ui) {
            val result = deleteFactor(factor)
            callback(result)
        }
    }
}

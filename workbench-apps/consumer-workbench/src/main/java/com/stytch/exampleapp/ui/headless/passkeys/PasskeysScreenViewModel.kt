package com.stytch.exampleapp.ui.headless.passkeys

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passkeys.Passkeys
import com.stytch.sdk.consumer.userManagement.UserAuthenticationFactor
import kotlinx.coroutines.launch

class PasskeysScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    private var currentPasskeyRegistrationId: String? = null

    fun clearPasskeyRegistrations() {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            StytchClient.user.getSyncUser()?.let {
                it.webauthnRegistrations.forEach {
                    reportState(
                        HeadlessMethodResponseState.Response(
                            StytchClient.user.deleteFactor(
                                factor = UserAuthenticationFactor.WebAuthn(id = it.id),
                            ),
                        ),
                    )
                }
            }
            currentPasskeyRegistrationId = null
        }
    }

    fun registerPasskey(
        activity: FragmentActivity,
        passkeysDomain: String,
    ) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchClient.passkeys.register(
                    Passkeys.RegisterParameters(
                        activity = activity,
                        domain = passkeysDomain,
                    ),
                )
            currentPasskeyRegistrationId = (response as? StytchResult.Success)?.value?.webauthnRegistrationId
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    fun authenticatePasskey(
        activity: FragmentActivity,
        passkeysDomain: String,
    ) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchClient.passkeys.authenticate(
                    Passkeys.AuthenticateParameters(
                        activity = activity,
                        domain = passkeysDomain,
                    ),
                )
            currentPasskeyRegistrationId =
                (response as? StytchResult.Success)
                    ?.value
                    ?.user
                    ?.webauthnRegistrations
                    ?.firstOrNull()
                    ?.id
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    fun updatePasskey(passkeyName: String) {
        val registrationId = currentPasskeyRegistrationId ?: return
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            reportState(
                HeadlessMethodResponseState.Response(
                    StytchClient.passkeys.update(
                        Passkeys.UpdateParameters(
                            id = registrationId,
                            name = passkeyName,
                        ),
                    ),
                ),
            )
        }
    }
}

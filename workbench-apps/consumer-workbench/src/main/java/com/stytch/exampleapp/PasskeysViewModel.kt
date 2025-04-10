package com.stytch.exampleapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.passkeys.Passkeys
import com.stytch.sdk.consumer.userManagement.UserAuthenticationFactor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasskeysViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String>
        get() = _currentResponse

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean>
        get() = _loadingState

    private val _currentPasskeyRegistrationId = MutableStateFlow("")
    val currentPasskeyRegistrationId: StateFlow<String>
        get() = _currentPasskeyRegistrationId

    var passkeyNameState by mutableStateOf(TextFieldValue(""))

    fun clearPasskeyRegistrations() {
        viewModelScope
            .launch {
                _loadingState.value = true
                when (val user = StytchClient.user.getUser()) {
                    is StytchResult.Success -> {
                        user.value.webauthnRegistrations.forEach {
                            val response =
                                StytchClient.user.deleteFactor(
                                    factor = UserAuthenticationFactor.WebAuthn(id = it.id),
                                )
                            println(response)
                        }
                    }
                    is StytchResult.Error -> println(user.exception)
                }
                _currentResponse.value = "Cleared all registrations"
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun registerPasskey(activity: FragmentActivity) {
        viewModelScope
            .launch {
                _loadingState.value = true
                val result =
                    StytchClient.passkeys.register(
                        Passkeys.RegisterParameters(
                            activity = activity,
                            domain = BuildConfig.PASSKEYS_DOMAIN,
                        ),
                    )
                _currentResponse.value = result.toFriendlyDisplay()
                _currentPasskeyRegistrationId.value =
                    when (result) {
                        is StytchResult.Success -> result.value.webauthnRegistrationId
                        else -> ""
                    }
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun authenticatePasskey(activity: FragmentActivity) {
        viewModelScope
            .launch {
                _loadingState.value = true
                val result =
                    StytchClient.passkeys.authenticate(
                        Passkeys.AuthenticateParameters(
                            activity = activity,
                            domain = BuildConfig.PASSKEYS_DOMAIN,
                        ),
                    )
                _currentPasskeyRegistrationId.value =
                    when (result) {
                        is StytchResult.Success ->
                            result.value.user.webauthnRegistrations[0]
                                .id
                        else -> ""
                    }
                _currentResponse.value = result.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }

    fun updatePasskey() {
        viewModelScope
            .launch {
                _loadingState.value = true
                val result =
                    StytchClient.passkeys.update(
                        Passkeys.UpdateParameters(
                            id = _currentPasskeyRegistrationId.value,
                            name = passkeyNameState.text,
                        ),
                    )
                _currentResponse.value = result.toFriendlyDisplay()
            }.invokeOnCompletion {
                _loadingState.value = false
            }
    }
}

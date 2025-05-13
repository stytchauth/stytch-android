package com.stytch.exampleapp.ui.headless.biometrics

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.Biometrics
import kotlinx.coroutines.launch

class BiometricsScreenViewModel(
    val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    init {
        println("JORDAN >>> initted")
    }

    fun registerBiometrics(
        context: FragmentActivity,
        promptData: Biometrics.PromptData? = null,
    ) {
        viewModelScope
            .launch {
                reportState(HeadlessMethodResponseState.Loading)
                val result =
                    StytchClient.biometrics.register(
                        Biometrics.RegisterParameters(
                            context = context,
                            promptData = promptData,
                            allowFallbackToCleartext = false,
                            allowDeviceCredentials = true,
                        ),
                    )
                reportState(HeadlessMethodResponseState.Response(result))
            }
    }

    fun authenticateBiometrics(
        context: FragmentActivity,
        promptData: Biometrics.PromptData? = null,
    ) {
        viewModelScope
            .launch {
                val result =
                    StytchClient.biometrics.authenticate(
                        Biometrics.AuthenticateParameters(context = context, promptData = promptData),
                    )
                reportState(HeadlessMethodResponseState.Response(result))
            }
    }

    fun removeRegistration() {
        viewModelScope.launch {
            StytchClient.biometrics.removeRegistration()
        }
    }
}

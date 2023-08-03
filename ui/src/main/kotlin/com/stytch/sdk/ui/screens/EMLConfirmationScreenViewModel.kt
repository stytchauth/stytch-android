package com.stytch.sdk.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import kotlinx.coroutines.launch

internal class EMLConfirmationScreenViewModel : ViewModel() {
    fun resendEML(parameters: MagicLinks.EmailMagicLinks.Parameters) {
        viewModelScope.launch {
            StytchClient.magicLinks.email.loginOrCreate(parameters)
        }
    }
}
package com.stytch.exampleapp.ui.headless.magicLinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import kotlinx.coroutines.launch

class MagicLinksScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun emlLoginOrCreate(emailAddress: String) {
        viewModelScope.launch {
            reportState(HeadlessMethodResponseState.Loading)
            val result =
                StytchClient.magicLinks.email.loginOrCreate(
                    MagicLinks.EmailMagicLinks.Parameters(
                        email = emailAddress,
                        loginMagicLinkUrl = "app://consumerworkbench?type=login",
                        signupMagicLinkUrl = "app://consumerworkbench?type=signup",
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(result))
        }
    }
}

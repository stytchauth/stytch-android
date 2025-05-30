package com.stytch.exampleapp.b2b.ui.headless.magicLinks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MagicLinksScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel() {
    fun handle(action: MagicLinksAction) =
        when (action) {
            is MagicLinksAction.SendEmailDiscovery -> sendEmailDiscovery(action.emailAddress)
            is MagicLinksAction.SendEmailInvite -> sendEmailInvite(action.emailAddress)
            is MagicLinksAction.SendEmailLoginOrSignup ->
                sendEmailLoginOrSignup(
                    action.organizationId,
                    action.emailAddress,
                )
        }

    private fun sendEmailDiscovery(emailAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.magicLinks.email.discoverySend(
                    B2BMagicLinks.EmailMagicLinks.DiscoverySendParameters(
                        emailAddress = emailAddress,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun sendEmailInvite(emailAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.magicLinks.email.invite(
                    B2BMagicLinks.EmailMagicLinks.InviteParameters(
                        emailAddress = emailAddress,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }

    private fun sendEmailLoginOrSignup(
        organizationId: String,
        emailAddress: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            reportState(HeadlessMethodResponseState.Loading)
            val response =
                StytchB2BClient.magicLinks.email.loginOrSignup(
                    B2BMagicLinks.EmailMagicLinks.Parameters(
                        organizationId = organizationId,
                        email = emailAddress,
                    ),
                )
            reportState(HeadlessMethodResponseState.Response(response))
        }
    }
}

sealed interface MagicLinksAction {
    data class SendEmailLoginOrSignup(
        val organizationId: String,
        val emailAddress: String,
    ) : MagicLinksAction

    data class SendEmailDiscovery(
        val emailAddress: String,
    ) : MagicLinksAction

    data class SendEmailInvite(
        val emailAddress: String,
    ) : MagicLinksAction
}

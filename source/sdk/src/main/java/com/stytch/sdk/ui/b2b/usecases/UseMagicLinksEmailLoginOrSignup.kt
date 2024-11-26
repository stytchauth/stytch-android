package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.magicLinks.B2BMagicLinks
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseMagicLinksEmailLoginOrSignup(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val request: PerformRequest<BasicData>,
) {
    operator fun invoke() {
        val organizationId =
            state.value.mfaPrimaryInfoState?.organizationId ?: state.value.activeOrganization?.organizationId ?: return
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.magicLinks.email.loginOrSignup(
                    B2BMagicLinks.EmailMagicLinks.Parameters(
                        email = state.value.emailState.emailAddress,
                        organizationId = organizationId,
                        loginRedirectUrl = "", // TODO
                        signupRedirectUrl = "", // TODO
                        loginTemplateId = "", // TODO
                        signupTemplateId = "", // TODO
                    ),
                )
            }
        }
    }
}

package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.DiscoveryAuthenticateResponseData
import com.stytch.sdk.b2b.oauth.OAuth.Discovery.DiscoveryAuthenticateParameters
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetDiscoveredOrganizations
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.UpdateEmailState
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseOAuthDiscoveryAuthenticate(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val request: PerformRequest<DiscoveryAuthenticateResponseData>,
) {
    operator fun invoke(token: String) {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.oauth.discovery.authenticate(
                    DiscoveryAuthenticateParameters(
                        discoveryOauthToken = token,
                    ),
                )
            }.onSuccess {
                dispatch(
                    UpdateEmailState(
                        state.value.emailState.copy(
                            emailAddress = it.emailAddress,
                            validEmail = it.emailAddress.isValidEmailAddress(),
                        ),
                    ),
                )
                dispatch(SetDiscoveredOrganizations(it.discoveredOrganizations))
                dispatch(SetNextRoute(Routes.Discovery))
            }
        }
    }
}

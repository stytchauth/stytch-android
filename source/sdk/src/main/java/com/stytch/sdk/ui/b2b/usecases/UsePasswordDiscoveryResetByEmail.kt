package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BPasswordDiscoveryResetByEmailResponseData
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetDeeplinkTokenPair
import com.stytch.sdk.ui.b2b.data.SetDiscoveredOrganizations
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UsePasswordDiscoveryResetByEmail(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val request: PerformRequest<B2BPasswordDiscoveryResetByEmailResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.passwords.discovery.resetByEmail(
                    Passwords.Discovery.ResetByEmailParameters(
                        passwordResetToken = state.value.deeplinkTokenPair?.token ?: "",
                        password = state.value.passwordState.password,
                    ),
                )
            }.onSuccess {
                dispatch(SetDeeplinkTokenPair(null))
                dispatch(SetDiscoveredOrganizations(it.discoveredOrganizations))
                dispatch(SetNextRoute(Routes.Discovery))
            }.onFailure {
                dispatch(SetDeeplinkTokenPair(null))
                dispatch(SetB2BError(B2BErrorType.Default))
            }
        }
    }
}

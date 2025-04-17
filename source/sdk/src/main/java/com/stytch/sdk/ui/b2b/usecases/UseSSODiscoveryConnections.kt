package com.stytch.sdk.ui.b2b.usecases

import androidx.core.text.htmlEncode
import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.B2BSSODiscoveryConnectionResponseData
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetDiscoveredSSOConnections
import com.stytch.sdk.ui.b2b.data.SetGenericError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UseSSODiscoveryConnections(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val request: PerformRequest<B2BSSODiscoveryConnectionResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.sso.discoverConnections(
                    emailAddress =
                        state.value.emailState.emailAddress
                            .htmlEncode(),
                )
            }.onSuccess {
                dispatch(SetDiscoveredSSOConnections(it.connections))
            }.onFailure {
                dispatch(SetGenericError(it.message))
            }
        }
    }
}

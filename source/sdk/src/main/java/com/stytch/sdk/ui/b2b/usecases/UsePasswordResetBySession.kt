package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.SessionResetResponseData
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UsePasswordResetBySession(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val request: PerformRequest<SessionResetResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.passwords.resetBySession(
                    Passwords.ResetBySessionParameters(
                        organizationId = state.value.activeOrganization?.organizationId ?: "",
                        password = state.value.passwordState.password,
                    ),
                )
            }
        }
    }
}

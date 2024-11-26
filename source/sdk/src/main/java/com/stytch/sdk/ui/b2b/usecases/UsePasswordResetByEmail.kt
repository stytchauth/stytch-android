package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UsePasswordResetByEmail(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val request: PerformRequest<EmailResetResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.passwords.resetByEmail(
                    Passwords.ResetByEmailParameters(
                        token = state.value.token ?: "",
                        password = state.value.passwordState.password,
                    ),
                )
            }
        }
    }
}

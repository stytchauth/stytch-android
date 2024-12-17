package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.EmailResetResponseData
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetB2BError
import com.stytch.sdk.ui.b2b.data.SetDeeplinkTokenPair
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UsePasswordResetByEmail(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val request: PerformRequest<EmailResetResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.passwords.resetByEmail(
                    Passwords.ResetByEmailParameters(
                        token = state.value.deeplinkTokenPair?.token ?: "",
                        password = state.value.passwordState.password,
                    ),
                )
            }.onSuccess {
                dispatch(SetDeeplinkTokenPair(null))
            }.onFailure {
                dispatch(SetDeeplinkTokenPair(null))
                dispatch(SetB2BError(B2BErrorType.Default))
            }
        }
    }
}

package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.PasswordsAuthenticateResponseData
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UsePasswordAuthenticate(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<PasswordsAuthenticateResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.passwords.authenticate(
                    Passwords.AuthParameters(
                        organizationId = state.value.activeOrganization?.organizationId ?: "",
                        emailAddress = state.value.emailState.emailAddress,
                        password = state.value.passwordState.password,
                        sessionDurationMinutes = productConfig.sessionOptions.sessionDurationMinutes,
                    ),
                )
            }
        }
    }
}

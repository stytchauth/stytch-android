package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.common.network.models.BasicData
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UsePasswordResetByEmailStart(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val productConfig: StytchB2BProductConfig,
    private val request: PerformRequest<BasicData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.passwords.resetByEmailStart(
                    Passwords.ResetByEmailStartParameters(
                        organizationId = state.value.activeOrganization?.organizationId ?: "",
                        emailAddress = state.value.emailState.emailAddress,
                        loginRedirectUrl = getRedirectUrl(),
                        resetPasswordRedirectUrl = getRedirectUrl(),
                        resetPasswordTemplateId = productConfig.passwordOptions.resetPasswordTemplateId,
                    ),
                )
            }.onSuccess {
                dispatch(SetNextRoute(Routes.PasswordSetNewConfirmation))
            }
        }
    }
}

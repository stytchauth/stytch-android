package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.common.network.models.Feedback
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.PerformRequest
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetStytchError
import com.stytch.sdk.ui.b2b.data.UpdatePasswordState
import com.stytch.sdk.ui.shared.data.PasswordState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class UsePasswordsStrengthCheck(
    private val scope: CoroutineScope,
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
    private val request: PerformRequest<StrengthCheckResponseData>,
) {
    operator fun invoke() {
        scope.launch(Dispatchers.IO) {
            request {
                StytchB2BClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(
                        email = state.value.emailState.emailAddress,
                        password = state.value.passwordState.password,
                    ),
                )
            }.onSuccess {
                val newPasswordState = it.toPasswordState(state.value.passwordState)
                dispatch(UpdatePasswordState(newPasswordState))
            }.onFailure {
                dispatch(SetStytchError(it as StytchError))
            }
        }
    }

    private fun StrengthCheckResponseData.toPasswordState(state: PasswordState): PasswordState =
        state.copy(
            validPassword = validPassword,
            breachedPassword = breachedPassword,
            score = score,
            feedback =
                Feedback(
                    suggestions = zxcvbnFeedback.suggestions,
                    warning = zxcvbnFeedback.warning,
                    ludsRequirements = ludsFeedback,
                ),
        )
}

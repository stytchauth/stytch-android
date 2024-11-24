package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.b2b.StytchB2BClient
import com.stytch.sdk.b2b.network.models.StrengthCheckResponseData
import com.stytch.sdk.b2b.passwords.Passwords
import com.stytch.sdk.common.errors.StytchError
import com.stytch.sdk.common.network.models.Feedback
import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import com.stytch.sdk.ui.shared.data.PasswordState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class UsePasswordsStrengthCheck(
    val scope: CoroutineScope,
    override val stateMachine: B2BUIStateMachine,
) : BaseUseCase(scope, stateMachine) {
    operator fun invoke() {
        val state = stateMachine.state.value
        scope.launch {
            performGenericRequest {
                StytchB2BClient.passwords.strengthCheck(
                    Passwords.StrengthCheckParameters(
                        email = state.emailState.emailAddress,
                        password = state.passwordState.password,
                    ),
                )
            }.onSuccess {
                stateMachine.updatePasswordState(it.toPasswordState(stateMachine.state.value.passwordState))
            }.onFailure {
                stateMachine.setStytchError(it as StytchError)
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

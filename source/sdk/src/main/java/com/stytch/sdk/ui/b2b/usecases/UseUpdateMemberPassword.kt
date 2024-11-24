package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import kotlinx.coroutines.CoroutineScope

internal class UseUpdateMemberPassword(
    val scope: CoroutineScope,
    override val stateMachine: B2BUIStateMachine,
) : BaseUseCase(scope, stateMachine) {
    operator fun invoke(password: String) {
        val state = stateMachine.state.value
        stateMachine.updatePasswordState(
            state.passwordState.copy(
                password = password,
            ),
        )
    }
}

package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.ui.b2b.domain.B2BUIStateMachine
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
import kotlinx.coroutines.CoroutineScope

internal class UseUpdateMemberEmailAddress(
    val scope: CoroutineScope,
    override val stateMachine: B2BUIStateMachine,
) : BaseUseCase(scope, stateMachine) {
    operator fun invoke(emailAddress: String) {
        val state = stateMachine.state.value
        stateMachine.updateEmailState(
            state.emailState.copy(
                emailAddress = emailAddress,
                validEmail = emailAddress.isValidEmailAddress(),
            ),
        )
    }
}

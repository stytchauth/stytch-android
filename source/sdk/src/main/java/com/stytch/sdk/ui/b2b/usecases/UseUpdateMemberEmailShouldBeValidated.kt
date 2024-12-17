package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.UpdateEmailState
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
import kotlinx.coroutines.flow.StateFlow

internal class UseUpdateMemberEmailShouldBeValidated(
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
) {
    operator fun invoke(shouldBeValidated: Boolean) {
        dispatch(
            UpdateEmailState(
                state.value.emailState.copy(
                    shouldValidateEmail = shouldBeValidated,
                    validEmail =
                        if (shouldBeValidated) {
                            state.value.emailState.emailAddress
                                .isValidEmailAddress()
                        } else {
                            state.value.emailState.validEmail
                        },
                ),
            ),
        )
    }
}

package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.UpdateEmailState
import com.stytch.sdk.ui.shared.utils.isValidEmailAddress
import kotlinx.coroutines.flow.StateFlow

internal class UseUpdateMemberEmailAddress(
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
) {
    operator fun invoke(emailAddress: String) {
        dispatch(
            UpdateEmailState(
                state.value.emailState.copy(
                    emailAddress = emailAddress,
                    validEmail =
                        if (state.value.emailState.shouldValidateEmail) {
                            emailAddress.isValidEmailAddress()
                        } else {
                            null
                        },
                ),
            ),
        )
    }
}

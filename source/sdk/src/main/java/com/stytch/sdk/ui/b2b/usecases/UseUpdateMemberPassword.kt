package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.UpdatePasswordState
import kotlinx.coroutines.flow.StateFlow

internal class UseUpdateMemberPassword(
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
) {
    operator fun invoke(password: String) {
        dispatch(
            UpdatePasswordState(
                state.value.passwordState.copy(
                    password = password,
                ),
            ),
        )
    }
}

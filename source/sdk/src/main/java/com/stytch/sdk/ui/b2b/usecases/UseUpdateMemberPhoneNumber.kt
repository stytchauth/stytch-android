package com.stytch.sdk.ui.b2b.usecases

import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.UpdatePhoneNumberState
import kotlinx.coroutines.flow.StateFlow

internal class UseUpdateMemberPhoneNumber(
    private val state: StateFlow<B2BUIState>,
    private val dispatch: Dispatch,
) {
    operator fun invoke(
        countryCode: String?,
        phoneNumber: String?,
    ) {
        dispatch(
            UpdatePhoneNumberState(
                state.value.phoneNumberState.copy(
                    countryCode = countryCode ?: state.value.phoneNumberState.countryCode,
                    phoneNumber = phoneNumber ?: state.value.phoneNumberState.phoneNumber,
                ),
            ),
        )
    }
}

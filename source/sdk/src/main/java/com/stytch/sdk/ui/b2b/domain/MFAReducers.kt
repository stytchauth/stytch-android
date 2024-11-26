package com.stytch.sdk.ui.b2b.domain

import com.freeletics.flowredux.dsl.ChangedState
import com.freeletics.flowredux.dsl.State
import com.stytch.sdk.b2b.network.models.IB2BAuthDataWithMFA
import com.stytch.sdk.ui.b2b.data.B2BUIState

internal fun handleResponseThatNeedsAdditionalAuthentication(
    state: State<B2BUIState>,
    response: IB2BAuthDataWithMFA,
): ChangedState<B2BUIState> {
    // handle MFA or additional primary auth
    return state.mutate { copy() }
}

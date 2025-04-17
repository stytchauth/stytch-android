package com.stytch.sdk.ui.b2b.screens.error

import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import kotlinx.coroutines.flow.StateFlow

internal class ErrorScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction)

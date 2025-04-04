package com.stytch.sdk.ui.b2b.screens.emailMethodSelection

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.AuthFlowType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseEmailOTPLoginOrSignup
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksDiscoverySend
import com.stytch.sdk.ui.b2b.usecases.UseMagicLinksEmailLoginOrSignup
import kotlinx.coroutines.flow.StateFlow

internal class EmailMethodSelectionScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val useMagicLinksEmailLoginOrSignup =
        UseMagicLinksEmailLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useMagicLinksDiscoverySend =
        UseMagicLinksDiscoverySend(viewModelScope, productConfig, state, ::dispatch, ::request)
    private val useEmailOTPLoginOrSignup =
        UseEmailOTPLoginOrSignup(viewModelScope, state, ::dispatch, productConfig, ::request)
    private val useEmailOTPDiscoverySend =
        UseEmailOTPDiscoverySend(viewModelScope, state, ::dispatch, productConfig, ::request)

    fun selectMethod(method: AllowedAuthMethods) {
        if (method == AllowedAuthMethods.MAGIC_LINK) {
            if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
                useMagicLinksEmailLoginOrSignup()
            } else {
                useMagicLinksDiscoverySend()
            }
        } else if (method == AllowedAuthMethods.EMAIL_OTP) {
            if (state.value.authFlowType == AuthFlowType.ORGANIZATION) {
                useEmailOTPLoginOrSignup()
            } else {
                useEmailOTPDiscoverySend()
            }
        }
    }
}

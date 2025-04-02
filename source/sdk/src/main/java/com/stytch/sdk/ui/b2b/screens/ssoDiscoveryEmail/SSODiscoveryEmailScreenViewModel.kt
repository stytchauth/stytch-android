package com.stytch.sdk.ui.b2b.screens.ssoDiscoveryEmail

import androidx.lifecycle.viewModelScope
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.usecases.UseSSODiscoveryConnections
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailAddress
import com.stytch.sdk.ui.b2b.usecases.UseUpdateMemberEmailShouldBeValidated
import kotlinx.coroutines.flow.StateFlow

internal class SSODiscoveryEmailScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    private val useUpdateMemberEmailAddress = UseUpdateMemberEmailAddress(state, ::dispatch)
    private val useUpdateMemberEmailShouldBeValidated = UseUpdateMemberEmailShouldBeValidated(state, ::dispatch)
    private val useSSODiscoveryConnections = UseSSODiscoveryConnections(viewModelScope, state, ::dispatch, ::request)

    fun handle(action: SSODiscoveryEmailScreenAction) {
        when (action) {
            SSODiscoveryEmailScreenAction.ResetEverything -> dispatch(ResetEverything)
            SSODiscoveryEmailScreenAction.SetEmailShouldBeValidated ->
                useUpdateMemberEmailShouldBeValidated(true)
            is SSODiscoveryEmailScreenAction.UpdateMemberEmailAddress ->
                useUpdateMemberEmailAddress(action.emailAddress)
            SSODiscoveryEmailScreenAction.UseSSODiscoveryConnections -> useSSODiscoveryConnections()
        }
    }
}

internal sealed class SSODiscoveryEmailScreenAction {
    @JacocoExcludeGenerated
    data class UpdateMemberEmailAddress(
        val emailAddress: String,
    ) : SSODiscoveryEmailScreenAction()

    data object ResetEverything : SSODiscoveryEmailScreenAction()

    data object SetEmailShouldBeValidated : SSODiscoveryEmailScreenAction()

    data object UseSSODiscoveryConnections : SSODiscoveryEmailScreenAction()
}

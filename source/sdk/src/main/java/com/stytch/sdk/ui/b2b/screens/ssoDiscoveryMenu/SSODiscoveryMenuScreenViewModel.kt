package com.stytch.sdk.ui.b2b.screens.ssoDiscoveryMenu

import android.app.Activity
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.b2b.usecases.UseSSOStart
import kotlinx.coroutines.flow.StateFlow

internal class SSODiscoveryMenuScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    private val useSSOStart = UseSSOStart()

    fun handle(action: SSODiscoveryMenuScreenAction) {
        when (action) {
            SSODiscoveryMenuScreenAction.ResetEverything -> dispatch(ResetEverything)
            is SSODiscoveryMenuScreenAction.SSOStart -> useSSOStart(action.activity, action.connectionId)
        }
    }
}

internal sealed class SSODiscoveryMenuScreenAction {
    data object ResetEverything : SSODiscoveryMenuScreenAction()

    @JacocoExcludeGenerated
    data class SSOStart(
        val activity: Activity,
        val connectionId: String,
    ) : SSODiscoveryMenuScreenAction()
}

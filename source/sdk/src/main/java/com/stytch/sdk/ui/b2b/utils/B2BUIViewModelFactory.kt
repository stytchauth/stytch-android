package com.stytch.sdk.ui.b2b.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import kotlinx.coroutines.flow.StateFlow

internal class B2BUIViewModelFactory(
    private val state: StateFlow<B2BUIState>,
    private val dispatchAction: suspend (B2BUIAction) -> Unit,
    private val productConfig: StytchB2BProductConfig,
) : ViewModelProvider.AndroidViewModelFactory() {
    private val stateFlowType = StateFlow::class.java
    private val dispatchType = Function2::class.java
    private val productConfigType = StytchB2BProductConfig::class.java

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        try {
            modelClass
                .getConstructor(stateFlowType, dispatchType, productConfigType)
                .newInstance(state, dispatchAction, productConfig)
        } catch (_: NoSuchMethodException) {
            modelClass
                .getConstructor(stateFlowType, dispatchType)
                .newInstance(state, dispatchAction)
        } catch (_: NoSuchMethodException) {
            super.create(modelClass)
        }
}

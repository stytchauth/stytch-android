package com.stytch.sdk.ui.b2b.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.screens.MainScreenViewModel
import kotlinx.coroutines.flow.StateFlow

internal class B2BUIViewModelFactory(
    private val state: StateFlow<B2BUIState>,
    private val dispatchAction: suspend (B2BUIAction) -> Unit,
    private val productConfig: StytchB2BProductConfig,
) : ViewModelProvider.AndroidViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when (modelClass) {
            MainScreenViewModel::class.java -> MainScreenViewModel(state, dispatchAction, productConfig) as T
            else -> super.create(modelClass)
        }
}

package com.stytch.sdk.ui.b2b

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig

internal class B2BAuthenticationViewModel(
    // expose the savedstatehandle so that all viewmodels use the same top-level one
    val savedStateHandle: SavedStateHandle,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(savedStateHandle, productConfig) {
    companion object {
        fun create(productConfig: StytchB2BProductConfig): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    B2BAuthenticationViewModel(createSavedStateHandle(), productConfig)
                }
            }
    }
}

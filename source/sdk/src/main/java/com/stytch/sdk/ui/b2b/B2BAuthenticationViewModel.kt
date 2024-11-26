package com.stytch.sdk.ui.b2b

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

internal class B2BAuthenticationViewModel(
    // expose the savedstatehandle so that all viewmodels use the same top-level one
    val savedStateHandle: SavedStateHandle,
) : BaseViewModel(savedStateHandle) {
    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    B2BAuthenticationViewModel(createSavedStateHandle())
                }
            }
    }
}

package com.stytch.sdk.ui.b2b.utils

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

public class B2BUIViewModelFactory(
    private val savedStateHandle: SavedStateHandle,
) : ViewModelProvider.AndroidViewModelFactory() {
    public override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when (modelClass) {
            // TestScreenViewModel::class.java -> TestScreenViewModel(savedStateHandle) as T
            else -> super.create(modelClass)
        }
}

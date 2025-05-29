package com.stytch.exampleapp.b2b.ui.headless.scim

import androidx.lifecycle.ViewModel
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

class SCIMScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel()

package com.stytch.exampleapp.b2b.ui.headless.oauth

import androidx.lifecycle.ViewModel
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

class OAuthScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel()

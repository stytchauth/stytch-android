package com.stytch.exampleapp.b2b.ui.headless.member

import androidx.lifecycle.ViewModel
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

class MemberScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel()

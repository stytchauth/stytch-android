package com.stytch.exampleapp.b2b.ui.headless.recoveryCodes

import androidx.lifecycle.ViewModel
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

class RecoveryCodesScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel()

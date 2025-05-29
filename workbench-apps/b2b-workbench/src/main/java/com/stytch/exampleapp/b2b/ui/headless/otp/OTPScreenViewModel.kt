package com.stytch.exampleapp.b2b.ui.headless.otp

import androidx.lifecycle.ViewModel
import com.stytch.exampleapp.b2b.ui.headless.HeadlessMethodResponseState

class OTPScreenViewModel(
    private val reportState: (HeadlessMethodResponseState) -> Unit,
) : ViewModel()

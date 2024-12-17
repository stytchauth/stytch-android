package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.usecases.UseRecoveryCodesRecover
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.components.StytchInput
import kotlinx.coroutines.flow.StateFlow

internal class RecoveryCodesEntryScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    val useRecoveryCodesRecover = UseRecoveryCodesRecover(viewModelScope, state, productConfig, ::request)
}

@Composable
internal fun RecoveryCodesEntryScreen(
    createViewModel: CreateViewModel<RecoveryCodesEntryScreenViewModel>,
    viewModel: RecoveryCodesEntryScreenViewModel = createViewModel(RecoveryCodesEntryScreenViewModel::class.java),
) {
    var recoveryCode by remember { mutableStateOf("") }
    Column {
        PageTitle(textAlign = TextAlign.Left, text = "Enter backup code")
        BodyText(text = "Enter one of the backup codes you saved when setting up your authenticator app.")
        StytchInput(modifier = Modifier.fillMaxWidth(), value = recoveryCode, onValueChange = { recoveryCode = it })
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            text = "Done",
            enabled = recoveryCode.isNotEmpty(),
        ) { viewModel.useRecoveryCodesRecover(recoveryCode) }
    }
}

package com.stytch.sdk.ui.b2b.screens.recoveryCodesEntry

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
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.components.StytchInput

@Composable
internal fun RecoveryCodesEntryScreen(viewModel: RecoveryCodesEntryScreenViewModel) {
    RecoveryCodesEntryScreenComposable(dispatch = viewModel::handle)
}

@Composable
private fun RecoveryCodesEntryScreenComposable(dispatch: (RecoveryCodesEntryAction) -> Unit) {
    var recoveryCode by remember { mutableStateOf("") }
    Column {
        PageTitle(textAlign = TextAlign.Left, text = "Enter backup code")
        BodyText(text = "Enter one of the backup codes you saved when setting up your authenticator app.")
        StytchInput(modifier = Modifier.fillMaxWidth(), value = recoveryCode, onValueChange = { recoveryCode = it })
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            text = "Done",
            enabled = recoveryCode.isNotEmpty(),
        ) { dispatch(RecoveryCodesEntryAction.Recover(recoveryCode)) }
    }
}

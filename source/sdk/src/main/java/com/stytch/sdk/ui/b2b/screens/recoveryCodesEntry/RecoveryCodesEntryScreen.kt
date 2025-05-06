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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
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
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_enter_backup_code))
        BodyText(text = stringResource(R.string.stytch_b2b_enter_one_of_the_backup_codes))
        StytchInput(modifier = Modifier.fillMaxWidth(), value = recoveryCode, onValueChange = { recoveryCode = it })
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            text = stringResource(R.string.stytch_b2b_done),
            enabled = recoveryCode.isNotEmpty(),
        ) { dispatch(RecoveryCodesEntryAction.Recover(recoveryCode)) }
    }
}

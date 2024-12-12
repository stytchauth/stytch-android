package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun EmailEntry(
    emailState: EmailState,
    keyboardActions: KeyboardActions? = null,
    onEmailAddressChanged: (String) -> Unit,
    onEmailAddressSubmit: () -> Unit,
) {
    EmailInput(
        emailState = emailState,
        onEmailAddressChanged = onEmailAddressChanged,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        label = stringResource(id = R.string.email),
        keyboardActions = keyboardActions,
    )
    StytchButton(
        onClick = onEmailAddressSubmit,
        modifier = Modifier.height(45.dp),
        text = stringResource(id = R.string.button_continue),
        enabled = !emailState.shouldValidateEmail || emailState.validEmail == true,
    )
}

@Preview
@Composable
private fun PreviewStytchInput() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            EmailEntry(
                onEmailAddressChanged = {},
                onEmailAddressSubmit = {},
                emailState = EmailState(validEmail = false),
            )
        }
    }
}

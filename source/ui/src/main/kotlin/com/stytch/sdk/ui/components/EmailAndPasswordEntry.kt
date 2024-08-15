package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.data.EmailState
import com.stytch.sdk.ui.data.PasswordState

@Composable
internal fun EmailAndPasswordEntry(
    emailState: EmailState,
    onEmailAddressChanged: (String) -> Unit,
    passwordState: PasswordState,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val isSubmittable = emailState.validEmail == true && passwordState.validPassword
    val semantics = stringResource(id = R.string.semantics_email_password_entry)
    Column(
        modifier = Modifier.semantics { contentDescription = semantics },
    ) {
        EmailInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
            emailState = emailState,
            onEmailAddressChanged = onEmailAddressChanged,
            label = stringResource(id = R.string.email),
        )
        PasswordInput(
            passwordState = passwordState,
            onPasswordChanged = onPasswordChanged,
            label = stringResource(id = R.string.password),
        )
        StytchButton(
            onClick = onSubmit,
            modifier = Modifier.height(45.dp),
            text = stringResource(id = R.string.button_continue),
            enabled = isSubmittable,
        )
    }
}

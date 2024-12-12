package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.data.EmailState
import com.stytch.sdk.ui.shared.data.PasswordState

@Composable
internal fun EmailAndPasswordEntry(
    emailState: EmailState,
    onEmailAddressChanged: (String) -> Unit,
    passwordState: PasswordState,
    onPasswordChanged: (String) -> Unit,
    allowInvalidSubmission: Boolean? = false,
    onSubmit: () -> Unit,
) {
    val isSubmittable = allowInvalidSubmission == true || (emailState.validEmail == true && passwordState.validPassword)
    val semantics = stringResource(id = R.string.semantics_email_password_entry)
    val localFocusManager = LocalFocusManager.current
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
            keyboardActions = KeyboardActions(onDone = { localFocusManager.moveFocus(FocusDirection.Next) }),
        )
        PasswordInput(
            passwordState = passwordState,
            onPasswordChanged = onPasswordChanged,
            label = stringResource(id = R.string.password),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        )
        StytchButton(
            onClick = onSubmit,
            modifier = Modifier.height(45.dp),
            text = stringResource(id = R.string.button_continue),
            enabled = isSubmittable,
        )
    }
}

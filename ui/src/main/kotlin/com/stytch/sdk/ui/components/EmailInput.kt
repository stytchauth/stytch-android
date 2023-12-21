package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.data.EmailState

@Composable
internal fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: EmailState,
    onEmailAddressChanged: (String) -> Unit,
    label: String? = null,
) {
    val emailAddress = emailState.emailAddress
    val isError = emailState.errorMessage != null || emailState.validEmail == false
    val semanticsInput = stringResource(id = R.string.semantics_email_input)
    val semanticsError = stringResource(id = R.string.semantics_email_error)
    Column {
        StytchInput(
            modifier = modifier.semantics { contentDescription = semanticsInput },
            value = emailAddress,
            onValueChange = onEmailAddressChanged,
            placeholder = "Email Address",
            isError = isError,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            label = label,
            readOnly = emailState.readOnly,
        )
        if (isError) {
            FormFieldStatus(
                modifier = Modifier.semantics { contentDescription = semanticsError },
                text = emailState.errorMessage ?: stringResource(id = R.string.invalid_email),
                isError = true,
            )
        }
    }
}

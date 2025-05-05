package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.data.EmailState

@Composable
internal fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: EmailState,
    label: String? = null,
    keyboardActions: KeyboardActions? = null,
    onEmailAddressChanged: (String) -> Unit,
) {
    val emailAddress = emailState.emailAddress
    val isError = emailState.errorMessage != null || emailState.validEmail == false
    val semanticsInput = stringResource(id = R.string.stytch_semantics_email_input)
    val semanticsError = stringResource(id = R.string.stytch_semantics_email_error)
    Column {
        StytchInput(
            modifier = modifier.semantics { contentDescription = semanticsInput },
            value = emailAddress,
            onValueChange = onEmailAddressChanged,
            placeholder = stringResource(id = R.string.stytch_email_address_placeholder),
            isError = isError,
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions = keyboardActions,
            label = label,
            readOnly = emailState.readOnly,
        )
        if (isError) {
            FormFieldStatus(
                modifier = Modifier.semantics { contentDescription = semanticsError },
                text =
                    emailState.errorMessage ?: stringResource(id = R.string.stytch_invalid_email_default_error_message),
                isError = true,
            )
        }
    }
}

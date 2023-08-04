package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    Column {
        StytchInput(
            modifier = modifier,
            value = emailAddress,
            onValueChange = onEmailAddressChanged,
            placeholder = "Email Address",
            isError = isError,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            label = label,
        )
        if (isError) {
            FormFieldStatus(
                text = emailState.errorMessage ?: stringResource(id = R.string.invalid_email),
                isError = true,
            )
        }
    }
}

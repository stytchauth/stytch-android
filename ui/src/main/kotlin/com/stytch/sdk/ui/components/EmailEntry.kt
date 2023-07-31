package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.R

@Composable
internal fun EmailEntry(
    emailAddress: String = "",
    onEmailAddressChanged: (String) -> Unit,
    emailAddressError: String? = null,
    onEmailAddressSubmit: () -> Unit,
    statusText: String? = null,
) {
    val isError = emailAddressError != null
    StytchInput(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        value = emailAddress,
        onValueChange = onEmailAddressChanged,
        placeholder = "Email Address",
        isError = isError,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
        )
    )
    if (statusText != null) {
        FormFieldStatus(
            text = statusText,
            isError = isError,
        )
    }
    StytchButton(
        onClick = onEmailAddressSubmit,
        modifier = Modifier.height(45.dp),
        text = stringResource(id = R.string.button_continue),
        enabled = emailAddress.isNotBlank() && emailAddressError == null,
    )
}

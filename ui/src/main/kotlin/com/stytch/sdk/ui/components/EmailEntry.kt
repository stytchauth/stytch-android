package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.theme.LocalStytchTheme

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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
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

@Preview
@Composable
private fun PreviewStytchInput() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            EmailEntry(onEmailAddressChanged = {}, onEmailAddressSubmit = { })
        }
    }
}

package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.data.PasswordState

@Composable
internal fun EmailAndPasswordEntry(
    emailAddress: String = "",
    onEmailAddressChanged: (String) -> Unit,
    passwordState: PasswordState,
    onPasswordChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isSubmittable = emailAddress.isNotEmpty() && passwordState.validPassword
    Column {
        StytchInput(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            value = emailAddress,
            onValueChange = onEmailAddressChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            label = stringResource(id = R.string.email)
        )
        StytchInput(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            value = passwordState.password,
            onValueChange = onPasswordChanged,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = stringResource(id = R.string.password),
            trailingIcon = {
                val icon = if (passwordVisible) {
                    Icons.Outlined.Visibility
                } else {
                    Icons.Outlined.VisibilityOff
                }
                val description = if (passwordVisible) {
                    stringResource(id = R.string.hide_password)
                } else {
                    stringResource(id = R.string.show_password)
                }
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        )
        StytchButton(
            onClick = onSubmit,
            modifier = Modifier.height(45.dp),
            text = stringResource(id = R.string.button_continue),
            enabled = isSubmittable,
        )
    }
}

package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography

@Composable
@Suppress("LongParameterList")
internal fun StytchInput(
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit = {},
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        readOnly = readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(theme.inputBackgroundColor),
            unfocusedContainerColor = Color(theme.inputBackgroundColor),
            disabledContainerColor = Color(theme.inputBackgroundColor),
            errorCursorColor = Color(theme.errorColor),
            focusedBorderColor = Color(theme.inputBorderColor),
            unfocusedBorderColor = Color(theme.inputBorderColor),
            errorBorderColor = Color(theme.errorColor),
        ),
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        keyboardOptions = keyboardOptions,
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = type.buttonLabel,
                color = Color(theme.inputPlaceholderTextColor),
            )
        },
        visualTransformation = visualTransformation,
        textStyle = type.buttonLabel.copy(
            textAlign = TextAlign.Start,
            color = Color(if (isError) theme.errorColor else theme.inputTextColor),
        ),
        isError = isError,
        trailingIcon = trailingIcon
    )
}

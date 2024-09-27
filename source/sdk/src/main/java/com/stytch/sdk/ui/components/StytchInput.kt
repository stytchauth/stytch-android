package com.stytch.sdk.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.Container
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography

@OptIn(ExperimentalMaterial3Api::class)
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
    trailingIcon: (@Composable () -> Unit)? = null,
    contentPadding: PaddingValues = OutlinedTextFieldDefaults.contentPadding(),
    textAlign: TextAlign = TextAlign.Start,
    label: String? = null,
) {
    val focusManager = LocalFocusManager.current
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val interactionSource = remember { MutableInteractionSource() }
    val colors =
        OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(theme.inputBackgroundColor),
            unfocusedContainerColor = Color(theme.inputBackgroundColor),
            disabledContainerColor = Color(theme.inputBackgroundColor),
            errorCursorColor = Color(theme.errorColor),
            focusedBorderColor = Color(theme.inputBorderColor),
            unfocusedBorderColor = Color(theme.inputBorderColor),
            errorBorderColor = Color(theme.errorColor),
            cursorColor = Color(theme.inputTextColor),
            focusedTrailingIconColor = Color(theme.inputTextColor),
            unfocusedTrailingIconColor = Color(theme.inputTextColor),
        )
    BasicTextField(
        cursorBrush = SolidColor(Color(if (isError) theme.errorColor else theme.inputTextColor)),
        value = value,
        modifier =
            modifier.defaultMinSize(
                minWidth = OutlinedTextFieldDefaults.MinWidth,
                minHeight = OutlinedTextFieldDefaults.MinHeight,
            ),
        onValueChange = onValueChange,
        enabled = true,
        readOnly = readOnly,
        textStyle =
            type.buttonLabel.copy(
                textAlign = textAlign,
                color = Color(if (isError) theme.errorColor else theme.inputTextColor),
            ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        interactionSource = interactionSource,
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                label = {
                    if (label != null) {
                        Text(
                            text = label,
                            style = type.labelText,
                            color = Color(theme.inputPlaceholderTextColor),
                        )
                    }
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        style = type.buttonLabel,
                        color = Color(theme.inputPlaceholderTextColor),
                    )
                },
                trailingIcon = trailingIcon,
                singleLine = true,
                enabled = true,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = contentPadding,
                container = {
                    Container(
                        enabled = true,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = OutlinedTextFieldDefaults.shape,
                    )
                },
            )
        },
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
            StytchInput(value = "Test")
        }
    }
}

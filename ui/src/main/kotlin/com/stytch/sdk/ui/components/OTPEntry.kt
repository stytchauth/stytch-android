package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.theme.LocalStytchTheme

private const val OTP_LENGTH = 6

@Composable
internal fun OTPEntry(
    errorMessage: String? = null,
    onCodeComplete: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val finalCode = remember { mutableStateListOf("", "", "", "", "", "") }
    val semantics = stringResource(id = R.string.semantics_otp_entry)
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
            .semantics { contentDescription = semantics },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        finalCode.asIterable().forEachIndexed { i, value ->
            var modifier = Modifier
                .width(48.dp)
                .height(48.dp)
            if (i == 0) {
                modifier = modifier.focusRequester(focusRequester)
            }
            StytchInput(
                modifier = modifier,
                contentPadding = PaddingValues(0.dp),
                value = value,
                isError = errorMessage != null,
                onValueChange = { newValue ->
                    if (newValue.length <= 1) {
                        finalCode[i] = newValue
                        if (finalCode.filter { it.isNotBlank() }.size == OTP_LENGTH) {
                            onCodeComplete(finalCode.joinToString(""))
                        }
                        if (newValue.length == 1 && i < (OTP_LENGTH - 1)) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        if (newValue.isEmpty() && i > 0) {
                            focusManager.moveFocus(FocusDirection.Previous)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
    if (errorMessage != null) {
        FormFieldStatus(text = errorMessage, isError = true)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewOTPEntry() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        Column(
            modifier = Modifier
                .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            OTPEntry {}
        }
    }
}

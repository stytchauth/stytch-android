package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.stytch.sdk.R
import com.stytch.sdk.common.network.models.StrengthPolicy
import com.stytch.sdk.ui.shared.data.PasswordState

@Composable
internal fun PasswordInput(
    passwordState: PasswordState,
    onPasswordChanged: (String) -> Unit,
    keyboardActions: KeyboardActions? = null,
    label: String? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordErrorString = passwordState.errorMessageId?.let { stringResource(it) } ?: passwordState.errorMessage
    Column {
        StytchInput(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            value = passwordState.password,
            onValueChange = onPasswordChanged,
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions = keyboardActions,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = label,
            trailingIcon = {
                val icon =
                    if (passwordVisible) {
                        Icons.Outlined.Visibility
                    } else {
                        Icons.Outlined.VisibilityOff
                    }
                val description =
                    if (passwordVisible) {
                        stringResource(id = R.string.stytch_semantics_hide_password)
                    } else {
                        stringResource(id = R.string.stytch_semantics_show_password)
                    }
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            },
        )
        if (passwordState.feedback != null) {
            when (passwordState.strengthPolicy) {
                StrengthPolicy.ZXCVBN -> {
                    PasswordStrengthIndicator(
                        feedback = passwordState.feedback,
                        score = passwordState.score,
                    )
                }
                StrengthPolicy.LUDS -> {
                    passwordState.feedback.ludsRequirements?.let {
                        LUDSIndicator(
                            requirements = it,
                            breached = passwordState.breachedPassword,
                        )
                    }
                }
            }
        }
        passwordErrorString?.let {
            FormFieldStatus(
                text = it,
                isError = true,
            )
        }
    }
}

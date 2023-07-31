package com.stytch.sdk.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import com.stytch.sdk.ui.utils.PhoneNumberVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PhoneEntry(
    countryCode: String,
    onCountryCodeChanged: (String) -> Unit,
    phoneNumber: String = "",
    onPhoneNumberChanged: (String) -> Unit,
    phoneNumberError: String = "",
    onPhoneNumberSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val isError = phoneNumberError.isNotBlank()
    val countryCodes = PhoneNumberUtil.getInstance().supportedCallingCodes.toList().sorted()
    val region = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(countryCode.toInt())
    val exampleNumber = PhoneNumberUtil.getInstance().getExampleNumber(region)?.nationalNumber?.toString() ?: ""
    val maxPhoneLengthForRegion = exampleNumber.length
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(0.33f),
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = "+$countryCode",
                    onValueChange = { },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(theme.inputBorderColor),
                        unfocusedBorderColor = Color(theme.inputBorderColor),
                        containerColor = Color(theme.inputBackgroundColor),
                    ),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                ) {
                    countryCodes.forEach { selectionOption ->
                        DropdownMenuItem(
                            onClick = {
                                onCountryCodeChanged(selectionOption.toString())
                                expanded = false
                            },
                            text = {
                                Text(
                                    text = "+$selectionOption",
                                    style = type.buttonLabel.copy(
                                        textAlign = TextAlign.Start,
                                        color = Color(theme.inputTextColor),
                                    ),
                                )
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    errorBorderColor = Color(theme.errorColor),
                    errorCursorColor = Color(theme.errorColor),
                    focusedBorderColor = Color(theme.inputBorderColor),
                    unfocusedBorderColor = Color(theme.inputBorderColor),
                    containerColor = Color(theme.inputBackgroundColor),
                ),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                ),
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= maxPhoneLengthForRegion) onPhoneNumberChanged(it)
                },
                placeholder = {
                    Text(
                        text = "(123) 456-7890",
                        style = type.buttonLabel,
                        color = Color(theme.inputPlaceholderTextColor),
                    )
                },
                visualTransformation = PhoneNumberVisualTransformation(region),
                textStyle = type.buttonLabel.copy(
                    textAlign = TextAlign.Start,
                    color = Color(if (isError) theme.errorColor else theme.inputTextColor),
                ),
                isError = isError,
            )
        }
        StytchButton(
            onClick = onPhoneNumberSubmit,
            modifier = Modifier.height(45.dp),
            text = stringResource(id = R.string.button_continue),
            enabled = phoneNumber.length >= maxPhoneLengthForRegion,
        )
    }
}

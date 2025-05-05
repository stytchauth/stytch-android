package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import com.stytch.sdk.ui.shared.utils.PhoneNumberVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongParameterList")
internal fun PhoneEntry(
    countryCode: String,
    onCountryCodeChanged: (String) -> Unit,
    phoneNumber: String = "",
    onPhoneNumberChanged: (String) -> Unit,
    phoneNumberError: String = "",
    onPhoneNumberSubmit: () -> Unit,
    statusText: String? = null,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val isError = phoneNumberError.isNotBlank()
    val countryCodes =
        PhoneNumberUtil
            .getInstance()
            .supportedCallingCodes
            .toList()
            .sorted()
    val region = PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(countryCode.toInt())
    val exampleNumber =
        PhoneNumberUtil
            .getInstance()
            .getExampleNumber(region)
            ?.nationalNumber
            ?.toString() ?: ""
    val maxPhoneLengthForRegion = exampleNumber.length
    var expanded by remember { mutableStateOf(false) }
    val semanticsPhoneInput = stringResource(id = R.string.stytch_semantics_phone_input)
    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
        ) {
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(0.33f),
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                StytchInput(
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
                    value = "+$countryCode",
                    readOnly = true,
                    isError = isError,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                        )
                    },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
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
                                    style =
                                        type.buttonLabel.copy(
                                            textAlign = TextAlign.Start,
                                            color = Color(theme.inputTextColor),
                                        ),
                                )
                            },
                            colors =
                                MenuDefaults.itemColors(
                                    textColor = Color(theme.primaryTextColor),
                                    trailingIconColor = Color(theme.primaryTextColor),
                                ),
                            modifier =
                                Modifier
                                    .background(color = Color(theme.disabledButtonBackgroundColor))
                                    .fillMaxSize(),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            StytchInput(
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = semanticsPhoneInput },
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= maxPhoneLengthForRegion) onPhoneNumberChanged(it)
                },
                visualTransformation = PhoneNumberVisualTransformation(region),
                isError = isError,
                placeholder = stringResource(id = R.string.stytch_default_formatted_phone_number_placeholder),
                keyboardOptions =
                    KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                    ),
            )
        }
        if (statusText != null) {
            FormFieldStatus(
                text = statusText,
                isError = isError,
            )
        }
        StytchButton(
            onClick = onPhoneNumberSubmit,
            modifier = Modifier.height(45.dp),
            text = stringResource(id = R.string.stytch_continue_button_text),
            enabled = phoneNumber.length >= maxPhoneLengthForRegion,
        )
    }
}

package com.stytch.stytchexampleapp.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stytch.stytchexampleapp.AuthenticationState
import com.stytch.stytchexampleapp.Route
import com.stytch.stytchexampleapp.ScreenState
import com.stytch.stytchexampleapp.utils.PhoneNumberVisualTransformation

@Composable
internal fun LoginRoute(
    authenticationState: AuthenticationState,
    navigateTo: (Route) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiStateFlow.collectAsState()
    when {
        !authenticationState.isInitialized -> {}
        authenticationState.userData != null -> navigateTo(Route.Profile)
        else ->
            LoginScreen(
                uiState = uiState.value,
                setPhoneNumber = viewModel::setPhoneNumber,
                setConfirmationCode = viewModel::setConfirmationCode,
                submitPhoneNumber = viewModel::submitPhoneNumber,
                confirmCode = viewModel::confirmCode,
                launchUILogin = { navigateTo(Route.UILogin) },
            )
    }
}

@Composable
private fun FormFieldRow(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    labelText: String,
    placeholderText: String,
    screenState: ScreenState,
    buttonEnabled: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            keyboardOptions = keyboardOptions,
            label = { Text(labelText, style = MaterialTheme.typography.bodySmall) },
            placeholder = { Text(placeholderText, style = MaterialTheme.typography.bodySmall) },
            visualTransformation = visualTransformation,
            textStyle = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.width(16.dp))
        if (screenState is ScreenState.Loading) {
            Box(modifier = Modifier.width(24.dp).height(24.dp)) {
                CircularProgressIndicator(modifier = Modifier.width(24.dp))
            }
        } else {
            IconButton(
                onClick = onSubmit,
                modifier = Modifier.width(24.dp),
                enabled = buttonEnabled,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowForward,
                    contentDescription = "Submit",
                    tint = if (buttonEnabled) Color.Black else Color.White,
                )
            }
        }
    }
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    setPhoneNumber: (String) -> Unit,
    setConfirmationCode: (String) -> Unit,
    submitPhoneNumber: () -> Unit,
    confirmCode: () -> Unit,
    launchUILogin: () -> Unit,
) {
    Column {
        if (!uiState.methodIdExists) {
            FormFieldRow(
                value = uiState.phoneNumber,
                onValueChange = {
                    if (it.length <= 10) setPhoneNumber(it)
                },
                labelText = "Phone Number",
                placeholderText = "(555) 555-0123",
                screenState = uiState.screenState,
                visualTransformation = PhoneNumberVisualTransformation("US"),
                onSubmit = submitPhoneNumber,
                keyboardOptions =
                    KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                    ),
                buttonEnabled = uiState.phoneNumberIsValid == true,
            )
        } else {
            FormFieldRow(
                value = uiState.confirmationCode,
                onValueChange = {
                    if (it.length <= 6) setConfirmationCode(it)
                },
                labelText = "OTP Code",
                placeholderText = "123456",
                screenState = uiState.screenState,
                onSubmit = confirmCode,
                keyboardOptions =
                    KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                buttonEnabled = uiState.confirmationCodeIsValid == true,
            )
        }
        if (uiState.screenState is ScreenState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.screenState.error.message ?: "Something went wrong",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = launchUILogin,
        ) {
            Text(
                text = "Try Stytch UI",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

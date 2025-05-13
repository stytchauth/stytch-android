package com.stytch.exampleapp.ui.shared

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.ui.ConsumerWorkbenchAppUIState
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.Biometrics
import kotlinx.coroutines.launch

@Composable
fun LoggedInView(state: ConsumerWorkbenchAppUIState.LoggedIn) {
    val context = LocalActivity.current as FragmentActivity
    val scope = rememberCoroutineScope()
    var biometricsAreAvailable = remember { mutableStateOf(false) }
    var canRegisterBiometrics = remember { mutableStateOf(false) }
    var canRemoveBiometrics = remember { mutableStateOf(false) }
    var canAddBiometricsToSession = remember { mutableStateOf(false) }
    var didDeleteBiometrics = remember { mutableStateOf(false) }

    LaunchedEffect(state.sessionData, didDeleteBiometrics) {
        biometricsAreAvailable.value = StytchClient.biometrics.isRegistrationAvailable(context)
        canRegisterBiometrics.value = !biometricsAreAvailable.value
        var hasAlreadyAuthedWithBiometrics = state.sessionData.authenticationFactors.any { it.biometricFactor != null }
        canRemoveBiometrics.value =
            biometricsAreAvailable.value &&
            hasAlreadyAuthedWithBiometrics &&
            state.sessionData.authenticationFactors.size >= 2
        canAddBiometricsToSession.value = biometricsAreAvailable.value && !hasAlreadyAuthedWithBiometrics
    }

    fun removeBiometrics() {
        scope.launch {
            StytchClient.biometrics.removeRegistration()
            didDeleteBiometrics.value = true
        }
    }

    fun addBiometrics() {
        scope.launch {
            StytchClient.biometrics.register(
                Biometrics.RegisterParameters(context = context),
            )
        }
    }

    fun authenticateBiometrics() {
        scope.launch {
            StytchClient.biometrics.authenticate(
                Biometrics.AuthenticateParameters(context = context),
            )
        }
    }

    fun logout() {
        scope.launch {
            StytchClient.sessions.revoke()
        }
    }

    Column {
        KeyValueRow(key = "User Id", value = state.userData.userId)
        KeyValueRow(
            key = "Phone Number",
            value =
                state.userData.phoneNumbers
                    .first()
                    .phoneNumber,
        )
        KeyValueRow(key = "Session Id", value = state.sessionData.sessionId)
        KeyValueRow(key = "Session Start", value = state.sessionData.startedAt)
        KeyValueRow(key = "Session Expires", value = state.sessionData.expiresAt)
        if (canRegisterBiometrics.value) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = ::addBiometrics,
                ) {
                    Text(
                        text = "Register biometrics",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        if (canRemoveBiometrics.value) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = ::removeBiometrics,
                ) {
                    Text(
                        text = "Delete biometrics",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        if (canAddBiometricsToSession.value) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = ::authenticateBiometrics,
                ) {
                    Text(
                        text = "Auth with biometrics",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = ::logout,
            ) {
                Text("Log Out")
            }
        }
    }
}

@Composable
private fun KeyValueRow(
    key: String,
    value: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Black),
        )
        Text(
            text = value.ifBlank { "Unknown" },
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

package com.stytch.exampleapp.ui.headless.biometrics

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.common.StytchObjectInfo
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.BiometricAvailability

@Composable
fun BiometricsScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = BiometricsScreenViewModel(reportState)
    BiometricsScreenComposable(
        register = viewModel::registerBiometrics,
        authenticate = viewModel::authenticateBiometrics,
        remove = viewModel::removeRegistration,
    )
}

@Composable
fun BiometricsScreenComposable(
    register: (FragmentActivity) -> Unit,
    authenticate: (FragmentActivity) -> Unit,
    remove: () -> Unit,
) {
    val context = LocalActivity.current as FragmentActivity
    var biometricsAreRegistered = remember { mutableStateOf(false) }
    var canRegisterBiometrics = remember { mutableStateOf(false) }
    var canRemoveBiometrics = remember { mutableStateOf(false) }
    var canAddBiometricsToSession = remember { mutableStateOf(false) }
    val biometricAvailability = remember { mutableStateOf<BiometricAvailability?>(null) }
    val sessionDataState = StytchClient.sessions.onChange.collectAsState()

    LaunchedEffect(sessionDataState.value) {
        val sessionData = (sessionDataState.value as? StytchObjectInfo.Available)?.value
        biometricsAreRegistered.value = StytchClient.biometrics.isRegistrationAvailable(context)
        canRegisterBiometrics.value = sessionData != null && !biometricsAreRegistered.value
        biometricAvailability.value = StytchClient.biometrics.areBiometricsAvailable(context, true)
        var hasAlreadyAuthedWithBiometrics =
            sessionData?.authenticationFactors?.any { it.biometricFactor != null } ?: false
        val hasAuthedWithMultipleMethods = (sessionData?.authenticationFactors?.size ?: 0) >= 2
        canRemoveBiometrics.value =
            biometricsAreRegistered.value &&
            hasAlreadyAuthedWithBiometrics &&
            hasAuthedWithMultipleMethods
        canAddBiometricsToSession.value = biometricsAreRegistered.value && !hasAlreadyAuthedWithBiometrics
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
        ) {
            OutlinedButton(
                enabled = canRegisterBiometrics.value,
                modifier = Modifier.fillMaxWidth(),
                onClick = { register(context) },
            ) {
                Text(
                    text = "Register biometrics",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
        ) {
            OutlinedButton(
                enabled = canRemoveBiometrics.value,
                modifier = Modifier.fillMaxWidth(),
                onClick = remove,
            ) {
                Text(
                    text = "Delete biometrics",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
        ) {
            OutlinedButton(
                enabled = canAddBiometricsToSession.value,
                modifier = Modifier.fillMaxWidth(),
                onClick = { authenticate(context) },
            ) {
                Text(
                    text = "Auth with biometrics",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        (biometricAvailability.value as? BiometricAvailability.Unavailable)?.let { availability ->
            Text(
                text = "Biometrics Unavailable on this device",
                modifier = Modifier.padding(8.dp),
            )
            if (
                (availability.reason == BiometricAvailability.Unavailable.Reason.BIOMETRIC_ERROR_NONE_ENROLLED) &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val enrollIntent =
                            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BiometricManager.Authenticators.BIOMETRIC_STRONG,
                                )
                            }
                        context.startActivityForResult(enrollIntent, 12345)
                    },
                ) {
                    Text("Enroll in biometrics")
                }
            }
        }
    }
}

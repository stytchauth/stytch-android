package com.stytch.exampleapp.ui.headless.biometrics

import android.R.attr.text
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.ui.headless.HeadlessMethodResponseState
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.BiometricAvailability

@Composable
fun BiometricsScreen(reportState: (HeadlessMethodResponseState) -> Unit) {
    val viewModel = BiometricsScreenViewModel(reportState)
    val context = LocalActivity.current as FragmentActivity
    val biometricAvailability = StytchClient.biometrics.areBiometricsAvailable(context, true)
    val biometricsAreAvailable = biometricAvailability !is BiometricAvailability.Unavailable
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (biometricsAreAvailable) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.registerBiometrics(context) },
            ) {
                Text("Register biometrics")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.authenticateBiometrics(context) },
            ) {
                Text("Authenticate biometrics")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.removeRegistration() },
            ) {
                Text("Remove biometrics")
            }
        } else {
            Text(
                text = "Biometrics Unavailable on this device",
                modifier = Modifier.padding(8.dp),
            )
            if (
                (biometricAvailability.reason == BiometricAvailability.Unavailable.Reason.BIOMETRIC_ERROR_NONE_ENROLLED) &&
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

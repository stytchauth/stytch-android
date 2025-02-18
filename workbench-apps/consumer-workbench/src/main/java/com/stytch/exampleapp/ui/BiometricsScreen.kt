package com.stytch.exampleapp.ui

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.stytch.exampleapp.BiometricsViewModel
import com.stytch.exampleapp.R
import com.stytch.sdk.consumer.StytchClient
import com.stytch.sdk.consumer.biometrics.BiometricAvailability

@Composable
fun BiometricsScreen(navController: NavController) {
    val viewModel: BiometricsViewModel = viewModel()
    val loading = viewModel.loadingState.collectAsState()
    val responseState = viewModel.currentResponse.collectAsState()
    val context = LocalActivity.current as FragmentActivity
    val biometricAvailability = StytchClient.biometrics.areBiometricsAvailable(context, true)
    val biometricsAreAvailable = biometricAvailability !is BiometricAvailability.Unavailable

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text =
                stringResource(
                    R.string.biometrics_registration_exists,
                    StytchClient.biometrics.isRegistrationAvailable(context),
                ),
        )
        if (biometricsAreAvailable) {
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.biometrics_register),
                onClick = { viewModel.registerBiometrics(context) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.biometrics_authenticate),
                onClick = { viewModel.authenticateBiometrics(context) },
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.biometrics_remove),
                onClick = { viewModel.removeRegistration() },
            )
        } else {
            val unavailable = biometricAvailability as BiometricAvailability.Unavailable
            Text(
                text = stringResource(id = R.string.biometrics_unavailable, unavailable.reason.message),
                modifier = Modifier.padding(8.dp),
            )
            if (
                (unavailable.reason == BiometricAvailability.Unavailable.Reason.BIOMETRIC_ERROR_NONE_ENROLLED) &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            ) {
                StytchButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.biometrics_enroll),
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
                )
            }
        }
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            Text(text = responseState.value, modifier = Modifier.padding(8.dp))
        }
    }
}

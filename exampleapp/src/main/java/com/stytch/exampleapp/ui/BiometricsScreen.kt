package com.stytch.exampleapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.stytch.exampleapp.BiometricsViewModel
import com.stytch.exampleapp.R
import com.stytch.sdk.StytchClient

@Composable
fun BiometricsScreen(navController: NavController) {
    val viewModel: BiometricsViewModel = viewModel()
    val loading = viewModel.loadingState.collectAsState()
    val responseState = viewModel.currentResponse.collectAsState()
    val context = LocalContext.current as FragmentActivity
    val biometricAvailability = StytchClient.biometrics.areBiometricsAvailable(context)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                R.string.biometrics_registration_exists,
                StytchClient.biometrics.registrationAvailable,
            )
        )
        if (biometricAvailability.available) {
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.biometrics_register),
                onClick = { viewModel.registerBiometrics(context) }
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.biometrics_authenticate),
                onClick = { viewModel.authenticateBiometrics(context) }
            )
            StytchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.biometrics_remove),
                onClick = { viewModel.removeRegistration() }
            )
        } else {
            Text(
                text = stringResource(id = R.string.biometrics_unavailable, biometricAvailability.message),
                modifier = Modifier.padding(8.dp)
            )
        }
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            Text(text = responseState.value, modifier = Modifier.padding(8.dp))
        }
    }
}

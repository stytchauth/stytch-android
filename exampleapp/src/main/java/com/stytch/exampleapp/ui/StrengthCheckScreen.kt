package com.stytch.exampleapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.stytch.exampleapp.R
import com.stytch.exampleapp.StrengthCheckViewModel

@Composable
fun StrengthCheckScreen(navController: NavController) {
    val viewModel: StrengthCheckViewModel = viewModel()
    val loading = viewModel.loadingState.collectAsState()
    val responseState = viewModel.currentResponse.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = viewModel.passwordTextState,
            singleLine = true,
            label = {
                Text(text = stringResource(id = R.string.password))
            },
            onValueChange = {
                viewModel.passwordTextState = it
            },
            shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize)
        )
        StytchButton(modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.check_password_strength),
            onClick = { viewModel.checkPassword() })
        if (loading.value) {
            CircularProgressIndicator()
        } else {
            Text(text = responseState.value, modifier = Modifier.padding(8.dp))
        }
    }
}

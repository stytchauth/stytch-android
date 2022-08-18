package com.stytch.exampleapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.R
import com.stytch.exampleapp.MainViewModel
import com.stytch.exampleapp.theme.Red300

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ExampleAppScreen(viewModel: MainViewModel = viewModel()) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    Scaffold(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(), contentColor = MaterialTheme.colors.onBackground, topBar = {
        Toolbar(toolbarText = stringResource(id = R.string.app_name))
    }, content = {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.emailTextState,
                isError = viewModel.showEmailError,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
                onValueChange = {
                    viewModel.emailTextState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize))
            if (viewModel.showEmailError) {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.please_enter_email),
                    style = MaterialTheme.typography.caption.copy(color = Red300),
                )
            }
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.phoneNumberTextState,
                isError = viewModel.showPhoneError,
                singleLine = true,
                label = {
                    Text(text = stringResource(id = R.string.phone_number))
                },
                onValueChange = {
                    viewModel.phoneNumberTextState = it
                },
                shape = MaterialTheme.shapes.small.copy(all = ZeroCornerSize))
            if (viewModel.showPhoneError) {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.please_enter_phone_number),
                    style = MaterialTheme.typography.caption.copy(color = Red300),
                )
            }
            StytchButton(modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.test_email_magic_link_flow),
                onClick = { viewModel.loginOrCreate() })
            if (loading.value) {
                CircularProgressIndicator()
            } else {
                Text(text = responseState.value, modifier = Modifier.padding(8.dp))
            }
        }
    })
}

@Composable
fun Toolbar(toolbarText: String) {
    TopAppBar(title = {
        Text(text = toolbarText, textAlign = TextAlign.Center, modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center))
    }, backgroundColor = MaterialTheme.colors.surface, elevation = 2.dp)
}

@Composable
fun StytchButton(modifier: Modifier = Modifier, text: String = "", onClick: () -> Unit = { /*TODO*/ }) {
    Button(modifier = modifier.padding(horizontal = 10.dp, vertical = 4.dp), onClick = onClick) {
        Text(text = text)
    }
}

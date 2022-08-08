package com.stytch.exampleapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.exampleapp.R
import com.stytch.exampleapp.SignInViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TestAppScreen(viewModel: SignInViewModel = viewModel()) {
    val responseState = viewModel.currentResponse.collectAsState()
    val loading = viewModel.loadingState.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        contentColor = MaterialTheme.colors.onBackground,
        topBar = {
            Toolbar(toolbarText = stringResource(id = R.string.app_name))
        },
        content = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                StytchButton(modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.authenticate),
                    onClick = { viewModel.authenticate() })
                StytchButton(modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.test_email_magic_link_flow),
                    onClick = { viewModel.loginOrCreate() })
                if (loading.value) {
                    CircularProgressIndicator()
                } else {
                    Text(text = responseState.value, modifier = Modifier.padding(8.dp))
                }
            }
        }
    )
}

@Composable
fun Toolbar(toolbarText: String) {
    TopAppBar(
        title = {
            Text(
                text = toolbarText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 2.dp
    )
}

@Composable
fun StytchButton(modifier: Modifier = Modifier, text: String = "", onClick: () -> Unit = { /*TODO*/ }) {
    Button(modifier = modifier.padding(horizontal = 10.dp, vertical = 4.dp), onClick = onClick) {
        Text(text = text)
    }
}

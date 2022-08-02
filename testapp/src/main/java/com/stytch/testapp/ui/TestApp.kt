package com.stytch.testapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.testapp.R

@Composable
fun TestApp() {
    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        contentColor = MaterialTheme.colors.onBackground,
        topBar = {
            Toolbar(toolbarText = stringResource(id = R.string.app_name))
        },
        content = { contentPadding ->
            Column() {
                StytchButton(modifier = Modifier.fillMaxWidth(), text = "Test Email Magic Link Direct API", onClick = { /*TODO*/ })
                StytchButton(modifier = Modifier.fillMaxWidth(), text = "Test Email Magic Link UI Flow", onClick = { /*TODO*/ })
                StytchButton(modifier = Modifier.fillMaxWidth(), text = "Test SMS Passcode UI Flow", onClick = { /*TODO*/ })
                StytchButton(modifier = Modifier.fillMaxWidth(), text = "Test something else", onClick = { /*TODO*/ })
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

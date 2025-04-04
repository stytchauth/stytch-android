package com.stytch.sdk.ui.b2b.screens.success

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.stytch.sdk.R

@Composable
internal fun SuccessScreen() {
    Column {
        Image(painter = painterResource(id = R.drawable.success), contentDescription = "Success")
    }
}

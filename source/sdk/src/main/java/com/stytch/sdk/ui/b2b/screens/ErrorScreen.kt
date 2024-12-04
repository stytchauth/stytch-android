package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle

@Composable
internal fun ErrorScreen(state: State<B2BUIState>) {
    val b2bError = state.value.b2BErrorType ?: return
    val orgName = state.value.activeOrganization?.organizationName ?: "the organization"
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PageTitle(text = "Looks like there was an error!")
        Image(
            painter = painterResource(id = R.drawable.big_error_circle),
            contentDescription = "Error",
        )
        Spacer(modifier = Modifier.height(32.dp))
        BodyText(text = b2bError.description.format(orgName), textAlign = TextAlign.Center)
    }
}

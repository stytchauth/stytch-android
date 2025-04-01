package com.stytch.sdk.ui.b2b.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.stytch.sdk.R
import com.stytch.sdk.b2b.network.models.SSOActiveConnection

@Composable
internal fun SSOActiveConnection.toPainterResource(): Painter =
    when (this.identityProvider) {
        "google-workspace" -> painterResource(id = R.drawable.google)
        "microsoft-entra" -> painterResource(id = R.drawable.microsoft)
        "okta" -> painterResource(id = R.drawable.okta)
        else -> painterResource(id = R.drawable.sso)
    }

package com.stytch.sdk.ui.b2b.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.data.B2BOAuthProviders

@Composable
internal fun B2BOAuthProviders.toPainterResource(): Painter =
    when (this) {
        B2BOAuthProviders.GOOGLE -> painterResource(id = R.drawable.google)
        B2BOAuthProviders.MICROSOFT -> painterResource(id = R.drawable.microsoft)
        B2BOAuthProviders.GITHUB -> painterResource(id = R.drawable.github)
        B2BOAuthProviders.SLACK -> painterResource(id = R.drawable.slack)
        B2BOAuthProviders.HUBSPOT -> painterResource(id = R.drawable.hubspot)
    }

internal fun B2BOAuthProviders.toTitle(): String =
    when (this) {
        B2BOAuthProviders.GOOGLE -> "Google"
        B2BOAuthProviders.MICROSOFT -> "Microsoft"
        B2BOAuthProviders.GITHUB -> "Github"
        B2BOAuthProviders.SLACK -> "Slack"
        B2BOAuthProviders.HUBSPOT -> "Hubspot"
    }

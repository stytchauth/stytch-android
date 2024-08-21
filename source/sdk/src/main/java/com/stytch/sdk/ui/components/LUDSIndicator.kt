package com.stytch.sdk.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.consumer.network.models.LUDSRequirements
import com.stytch.sdk.ui.theme.LocalStytchBootstrapData
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography

@Composable
internal fun LUDSIndicator(
    requirements: LUDSRequirements,
    breached: Boolean,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val bootstrapData = LocalStytchBootstrapData.current
    val validLength = requirements.missingCharacters == 0
    val validComplexity = requirements.missingComplexity == 0
    Column(
        modifier = Modifier.padding(bottom = 16.dp),
    ) {
        Row {
            Image(
                painter =
                    if (validLength) {
                        painterResource(id = R.drawable.checkicon)
                    } else {
                        painterResource(id = R.drawable.crossicon)
                    },
                contentDescription = null,
                modifier =
                    Modifier
                        .width(16.dp)
                        .padding(top = 6.dp, end = 4.dp),
            )
            Text(
                text =
                    stringResource(
                        id = R.string.missing_characters,
                        bootstrapData.passwordConfig?.ludsMinimumCount ?: 0,
                    ),
                style =
                    type.caption.copy(
                        color =
                            if (validLength) {
                                Color(theme.successColor)
                            } else {
                                Color(theme.errorColor)
                            },
                        textAlign = TextAlign.Start,
                    ),
            )
        }
        Row {
            Image(
                painter =
                    if (validComplexity) {
                        painterResource(id = R.drawable.checkicon)
                    } else {
                        painterResource(id = R.drawable.crossicon)
                    },
                contentDescription = null,
                modifier =
                    Modifier
                        .width(16.dp)
                        .padding(top = 6.dp, end = 4.dp),
            )
            Text(
                text =
                    stringResource(
                        id = R.string.missing_complexity,
                        bootstrapData.passwordConfig?.ludsComplexity ?: 0,
                    ),
                style =
                    type.caption.copy(
                        color =
                            if (validComplexity) {
                                Color(theme.successColor)
                            } else {
                                Color(theme.errorColor)
                            },
                        textAlign = TextAlign.Start,
                    ),
            )
        }
        if (breached) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.crossicon),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .width(16.dp)
                            .padding(end = 4.dp),
                )
                Text(
                    text = stringResource(id = R.string.breached_password_warning),
                    style =
                        type.caption.copy(
                            color = Color(theme.errorColor),
                            textAlign = TextAlign.Start,
                        ),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLUDSIndicator() {
    val requirements: LUDSRequirements =
        LUDSRequirements(
            hasLowerCase = false,
            hasUpperCase = false,
            hasDigit = false,
            hasSymbol = false,
            missingCharacters = 0,
            missingComplexity = 0,
        )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        LUDSIndicator(requirements, false)
    }
}

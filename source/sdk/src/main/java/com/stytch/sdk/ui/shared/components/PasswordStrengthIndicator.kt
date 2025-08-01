package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.common.network.models.Feedback
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import com.stytch.sdk.ui.shared.utils.mapZxcvbnToStringResource

private const val MAX_SCORE = 3

@Composable
internal fun PasswordStrengthIndicator(
    feedback: Feedback,
    score: Int,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    val emptyColor = Color(theme.inputBorderColor)
    val filledColor = Color(if (score < MAX_SCORE) theme.errorColor else theme.successColor)
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(MAX_SCORE + 1) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1F)
                            .height(4.dp)
                            .background(if (score >= it) filledColor else emptyColor),
                )
                if (it < MAX_SCORE) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
        if (score < MAX_SCORE) {
            if (feedback.warning.isNotBlank()) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.crossicon),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .width(16.dp)
                                .padding(top = 6.dp, end = 4.dp),
                        colorFilter = ColorFilter.tint(filledColor),
                    )
                    Text(
                        text = feedback.warning.mapZxcvbnToStringResource(),
                        style =
                            type.caption.copy(
                                color = filledColor,
                                textAlign = TextAlign.Start,
                            ),
                    )
                }
            }
            feedback.suggestions.map {
                Row {
                    Text(
                        text = "•",
                        style =
                            type.caption.copy(
                                color = Color(theme.secondaryTextColor),
                                textAlign = TextAlign.Center,
                            ),
                        modifier = Modifier.width(16.dp).padding(end = 4.dp),
                    )
                    Text(
                        text = it.mapZxcvbnToStringResource(),
                        style =
                            type.caption.copy(
                                color = Color(theme.secondaryTextColor),
                                textAlign = TextAlign.Start,
                            ),
                    )
                }
            }
        } else {
            Text(
                text = stringResource(id = R.string.stytch_feedback_good_password_message),
                style =
                    type.caption.copy(
                        color = filledColor,
                        textAlign = TextAlign.Start,
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewPasswordStrengthIndicator() {
    val mockFeedback =
        Feedback(
            suggestions = listOf("Repeats like \"aaa\" are easy to guess.", "Avoid recent years."),
            warning = "Names and surnames by themselves are easy to guess.",
            ludsRequirements = null,
        )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(LocalStytchTheme.current.backgroundColor),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(start = 32.dp, top = 64.dp, end = 32.dp, bottom = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            PasswordStrengthIndicator(feedback = mockFeedback, score = 2)
        }
    }
}

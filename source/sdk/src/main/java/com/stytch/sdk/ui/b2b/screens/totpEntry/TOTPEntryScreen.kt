package com.stytch.sdk.ui.b2b.screens.totpEntry

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.Body2Text
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.OTPEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

internal sealed class TOTPEntryScreenAction {
    data object GoToTOTPEnrollment : TOTPEntryScreenAction()

    data object UseRecoveryCode : TOTPEntryScreenAction()

    data object TextMeACode : TOTPEntryScreenAction()

    @JacocoExcludeGenerated
    data class ValidateCode(
        val code: String,
    ) : TOTPEntryScreenAction()
}

@Composable
internal fun TOTPEntryScreen(viewModel: TOTPEntryScreenViewModel) {
    val totpEntryState = viewModel.totpEntryState.collectAsStateWithLifecycle()
    TOTPEntryScreenComposable(
        state = totpEntryState.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun TOTPEntryScreenComposable(
    state: TOTPEntryScreenState,
    dispatch: (TOTPEntryScreenAction) -> Unit,
) {
    val theme = LocalStytchTheme.current
    BackHandler(enabled = state.isEnrolling) {
        dispatch(TOTPEntryScreenAction.GoToTOTPEnrollment)
    }
    Column {
        if (state.isEnrolling) {
            BackButton { dispatch(TOTPEntryScreenAction.GoToTOTPEnrollment) }
        }
        PageTitle(textAlign = TextAlign.Left, text = "Enter verification code")
        BodyText(text = "Enter the 6-digit code from your authenticator app.")
        OTPEntry(
            errorMessage = state.errorMessage,
            onCodeComplete = { dispatch(TOTPEntryScreenAction.ValidateCode(it)) },
        )

        if (!state.isEnrolling) {
            StytchTextButton(
                text =
                    buildAnnotatedString {
                        append("Can’t access your authenticator app? ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Use a backup code")
                        }
                    },
                color = theme.secondaryTextColor,
                onClick = { dispatch(TOTPEntryScreenAction.UseRecoveryCode) },
            )
        } else {
            Body2Text(
                text =
                    AnnotatedString(
                        "If the verification code doesn’t work, go back to your authenticator app to get a new code.",
                    ),
            )
        }

        if (state.isSmsOtpAvailable) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(theme.inputBorderColor),
            )
            StytchTextButton(text = "Text me a code instead", onClick = { dispatch(TOTPEntryScreenAction.TextMeACode) })
        }
    }
}

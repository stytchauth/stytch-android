package com.stytch.sdk.ui.b2b.screens.totpEntry

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.R
import com.stytch.sdk.common.annotations.JacocoExcludeGenerated
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.Body2Text
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.OTPEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.utils.getStyledText

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
    val context = LocalContext.current
    BackHandler(enabled = state.isEnrolling) {
        dispatch(TOTPEntryScreenAction.GoToTOTPEnrollment)
    }
    Column {
        if (state.isEnrolling) {
            BackButton { dispatch(TOTPEntryScreenAction.GoToTOTPEnrollment) }
        }
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_code_verification_title))
        BodyText(text = stringResource(R.string.stytch_b2b_totp_entry_body))
        OTPEntry(
            errorMessage = if (state.isInvalid) stringResource(R.string.stytch_b2b_error_invalid_totp) else null,
            onCodeComplete = { dispatch(TOTPEntryScreenAction.ValidateCode(it)) },
        )

        if (!state.isEnrolling) {
            StytchTextButton(
                text = stringResource(R.string.stytch_b2b_totp_entry_use_backup_code),
                color = theme.secondaryTextColor,
                onClick = { dispatch(TOTPEntryScreenAction.UseRecoveryCode) },
            )
        } else {
            Body2Text(text = context.getStyledText(R.string.stytch_b2b_totp_entry_get_new_code))
        }

        if (state.isSmsOtpAvailable) {
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(theme.inputBorderColor),
            )
            StytchTextButton(
                text = stringResource(R.string.stytch_b2b_mfa_totp_enrollment_switch_to_sms),
                onClick = { dispatch(TOTPEntryScreenAction.TextMeACode) },
            )
        }
    }
}

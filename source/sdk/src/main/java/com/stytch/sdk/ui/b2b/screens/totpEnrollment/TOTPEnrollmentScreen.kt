package com.stytch.sdk.ui.b2b.screens.totpEnrollment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.components.LoadingView
import com.stytch.sdk.ui.b2b.data.MFATOTPState
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun TOTPEnrollmentScreen(viewModel: TOTPEnrollmentScreenViewModel) {
    val totpState = viewModel.totpState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onScreenLoad()
    }
    TOTPEnrollmentScreenComposable(
        state = totpState.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun TOTPEnrollmentScreenComposable(
    state: MFATOTPState?,
    dispatch: (TOTPEnrollmentScreenAction) -> Unit,
) {
    val theme = LocalStytchTheme.current
    val clipboardManager = LocalClipboardManager.current
    if (state == null || state.isCreating) {
        return LoadingView(color = Color(theme.inputTextColor))
    }
    if (state.error != null) {
        return BodyText(text = state.error.message)
    }
    val secret = (state.enrollmentState?.secret ?: "").lowercase()
    val secretChunked = secret.chunked(4).joinToString(" ")
    var didCopyCode by remember { mutableStateOf(false) }
    BackHandler(enabled = true) {
        dispatch(TOTPEnrollmentScreenAction.GoToMFAEnrollment)
    }
    Column {
        BackButton(onClick = {
            dispatch(TOTPEnrollmentScreenAction.GoToMFAEnrollment)
        })
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_mfa_totp_enrollment_title))
        BodyText(text = stringResource(R.string.stytch_b2b_mfa_totp_enrollment_body))
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color(theme.disabledButtonBackgroundColor))
                    .border(
                        1.dp,
                        Color(theme.disabledButtonBackgroundColor),
                        RoundedCornerShape(theme.buttonBorderRadius),
                    ).clickable {
                        clipboardManager.setText(AnnotatedString(secret))
                        didCopyCode = true
                    },
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    text = secretChunked,
                    style =
                        TextStyle(
                            fontFamily = FontFamily(Font(R.font.ibm_plex_mono_regular)),
                            fontWeight = FontWeight.W400,
                            fontSize = 16.sp,
                            lineHeight = 20.sp,
                            textAlign = TextAlign.Center,
                            color = Color(theme.primaryTextColor),
                        ),
                )
                Icon(
                    modifier = Modifier.fillMaxWidth(0.2f),
                    painter = painterResource(id = R.drawable.copy),
                    contentDescription = null,
                    tint = Color(theme.primaryTextColor),
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            enabled = didCopyCode,
            text = stringResource(R.string.stytch_button_continue),
            onClick = { dispatch(TOTPEnrollmentScreenAction.GoToCodeEntry) },
        )
    }
}

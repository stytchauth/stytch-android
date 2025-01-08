package com.stytch.sdk.ui.b2b.components

import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.OTPEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchAlertDialog
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val OTP_EXPIRATION_SECONDS = (2 * 60).toLong()
private const val ONE_SECOND = 1000L

@Composable
internal fun ResendableOTP(
    title: String,
    recipient: AnnotatedString,
    isEnrolling: Boolean,
    onBack: (() -> Unit)? = null,
    onSubmit: (String) -> Unit,
    onResend: () -> Unit,
    errorMessage: String? = null,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    var showResendDialog by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableLongStateOf(OTP_EXPIRATION_SECONDS) }
    var expirationTimeFormatted by remember { mutableStateOf("2:00") }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (countdownSeconds > 0) {
                delay(ONE_SECOND)
                countdownSeconds -= 1
                expirationTimeFormatted = DateUtils.formatElapsedTime(countdownSeconds)
            }
        }
    }
    BackHandler(enabled = isEnrolling) { onBack?.invoke() }
    Column {
        if (isEnrolling) {
            BackButton { onBack?.invoke() }
        }
        PageTitle(textAlign = TextAlign.Left, text = title)
        BodyText(
            text =
                buildAnnotatedString {
                    append("A 6-digit passcode was sent to you at ")
                    append(recipient)
                },
        )
        OTPEntry(errorMessage = errorMessage, onCodeComplete = onSubmit)
        Text(
            text = stringResource(id = R.string.code_expires_in, expirationTimeFormatted),
            textAlign = TextAlign.Start,
            style =
                type.caption.copy(
                    color = Color(theme.secondaryTextColor),
                ),
            modifier = Modifier.clickable { showResendDialog = true },
        )
    }
    if (showResendDialog) {
        StytchAlertDialog(
            onDismissRequest = { showResendDialog = false },
            title = stringResource(id = R.string.resend_code),
            body =
                buildAnnotatedString {
                    append(stringResource(id = R.string.new_code_will_be_sent_to))
                    append(recipient)
                },
            cancelText = stringResource(id = R.string.cancel),
            onCancelClick = { showResendDialog = false },
            acceptText = stringResource(id = R.string.send_code),
            onAcceptClick = {
                onResend()
                countdownSeconds = OTP_EXPIRATION_SECONDS
                showResendDialog = false
            },
        )
    }
}

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.OTPEntry
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchAlertDialog
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import com.stytch.sdk.ui.shared.utils.getStyledText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val DEFAULT_OTP_EXIPRATION_MINUTES = 2
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
    otpExpirationMinutes: Int = DEFAULT_OTP_EXIPRATION_MINUTES,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    var showResendDialog by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableLongStateOf(otpExpirationMinutes * 60L) }
    var expirationTimeFormatted by remember { mutableStateOf("$otpExpirationMinutes:00") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
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
        BodyText(text = context.getStyledText(R.string.stytch_passcode_sent_to, recipient))
        OTPEntry(errorMessage = errorMessage, onCodeComplete = onSubmit)
        Text(
            text = stringResource(id = R.string.stytch_code_expires_in, expirationTimeFormatted),
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
            title = stringResource(id = R.string.stytch_resend_code_title),
            body = context.getStyledText(R.string.stytch_new_code_will_be_sent_to, recipient),
            cancelText = stringResource(id = R.string.stytch_cancel),
            onCancelClick = { showResendDialog = false },
            acceptText = stringResource(id = R.string.stytch_send_code),
            onAcceptClick = {
                onResend()
                countdownSeconds = otpExpirationMinutes * 60L
                showResendDialog = false
            },
        )
    }
}

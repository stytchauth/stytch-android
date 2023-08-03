package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.stytch.sdk.consumer.magicLinks.MagicLinks
import com.stytch.sdk.ui.R
import com.stytch.sdk.ui.components.BackButton
import com.stytch.sdk.ui.components.BodyText
import com.stytch.sdk.ui.components.PageTitle
import com.stytch.sdk.ui.components.StytchAlertDialog
import com.stytch.sdk.ui.theme.LocalStytchTheme
import com.stytch.sdk.ui.theme.LocalStytchTypography
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class EMLConfirmationScreen(
    val parameters: MagicLinks.EmailMagicLinks.Parameters
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<EMLConfirmationScreenViewModel>()
        EMLConfirmationScreenComposable(
            parameters = parameters,
            viewModel = viewModel
        )
    }
}

@Composable
private fun EMLConfirmationScreenComposable(
    parameters: MagicLinks.EmailMagicLinks.Parameters,
    viewModel: EMLConfirmationScreenViewModel,
) {
    val navigator = LocalNavigator.currentOrThrow
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    var showResendDialog by remember { mutableStateOf(false) }
    val recipientFormatted = AnnotatedString(
        text = " ${parameters.email}",
        spanStyle = SpanStyle(fontWeight = FontWeight.W700)
    )
    val resendCodeFormatted = AnnotatedString(
        text = stringResource(id = R.string.resend_code),
        spanStyle = SpanStyle(fontWeight = FontWeight.W700)
    )
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        BackButton { navigator.pop() }
        PageTitle(
            text = stringResource(id = R.string.check_your_email),
            textAlign = TextAlign.Start,
        )
        BodyText(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.login_link_sent))
                append(recipientFormatted)
            }
        )
        Text(
            text = buildAnnotatedString {
                append(stringResource(id = R.string.didnt_get_it))
                append(resendCodeFormatted)
            },
            textAlign = TextAlign.Start,
            style = type.caption.copy(
                color = Color(theme.secondaryTextColor)
            ),
            modifier = Modifier.clickable { showResendDialog = true }
        )
    }
    if (showResendDialog) {
        StytchAlertDialog(
            onDismissRequest = { showResendDialog = false },
            title = stringResource(id = R.string.resend_code),
            body = buildAnnotatedString {
                append(stringResource(id = R.string.new_code_will_be_sent_to))
                append(recipientFormatted)
            },
            cancelText = stringResource(id = R.string.cancel),
            onCancelClick = { showResendDialog = false },
            acceptText = stringResource(id = R.string.send_code),
            onAcceptClick = {
                showResendDialog = false
                viewModel.resendEML(parameters)
            }
        )
    }
}

package com.stytch.sdk.ui.b2b.screens.emailMethodSelection

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.b2b.network.models.AllowedAuthMethods
import com.stytch.sdk.ui.b2b.components.MenuItemWithRightArrow
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

private val EMAIL_METHODS = listOf(AllowedAuthMethods.EMAIL_OTP, AllowedAuthMethods.MAGIC_LINK)

@Composable
internal fun EmailMethodSelectionScreen(viewModel: EmailMethodSelectionScreenViewModel) {
    EmailMethodSelectionScreenComposable(dispatch = viewModel::selectMethod)
}

@Composable
private fun EmailMethodSelectionScreenComposable(dispatch: (AllowedAuthMethods) -> Unit) {
    val theme = LocalStytchTheme.current
    Column {
        PageTitle(
            textAlign = TextAlign.Left,
            text =
                stringResource(
                    R.string.stytch_b2b_email_method_selection_title,
                ),
        )
        Column {
            EMAIL_METHODS.forEach { method ->
                when (method) {
                    AllowedAuthMethods.EMAIL_OTP -> stringResource(R.string.stytch_b2b_email_method_selection_code)
                    AllowedAuthMethods.MAGIC_LINK -> stringResource(R.string.stytch_b2b_email_method_selection_link)
                    else -> null
                }?.let { title ->
                    MenuItemWithRightArrow(title = title, onClick = { dispatch(method) })
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(theme.inputBorderColor),
                    )
                }
            }
        }
    }
}

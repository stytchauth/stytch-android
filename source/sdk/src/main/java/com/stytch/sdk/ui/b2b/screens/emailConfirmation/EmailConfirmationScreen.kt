package com.stytch.sdk.ui.b2b.screens.emailConfirmation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchTextButton
import org.bouncycastle.asn1.x500.style.RFC4519Style.title

@Composable
internal fun EmailConfirmationScreen(
    route: EmailConfirmationScreenRoute,
    viewModel: EmailConfirmationScreenViewModel,
) {
    val state = viewModel.emailConfirmationScreenState.collectAsState()
    LaunchedEffect(route) {
        viewModel.route = route
    }
    when (val value = state.value) {
        EmailConfirmationScreenState.Loading -> {}
        is EmailConfirmationScreenState.Ready -> {
            EmailConfirmationScreenComposable(
                state = value,
                dispatch = viewModel::onBottomTextClicked,
            )
        }
    }
}

@Composable
private fun EmailConfirmationScreenComposable(
    state: EmailConfirmationScreenState.Ready,
    dispatch: () -> Unit,
) {
    Column {
        PageTitle(textAlign = TextAlign.Left, text = state.title)
        BodyText(text = state.message)
        StytchTextButton(text = state.bottomText.text, onClick = dispatch)
    }
}

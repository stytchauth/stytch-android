package com.stytch.sdk.ui.b2b.screens.ssoDiscoveryFallback

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.FormFieldStatus
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.components.StytchInput

@Composable
internal fun SSODiscoveryFallbackScreen(viewModel: SSODiscoveryFallbackScreenViewModel) {
    val state = viewModel.uiState.collectAsState()
    SSODiscoveryFallbackScreenComposable(
        state = state.value,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun SSODiscoveryFallbackScreenComposable(
    state: SSODiscoveryFallbackScreenUIState,
    dispatch: (SSODiscoveryFallbackScreenAction) -> Unit,
) {
    val (slug, setSlug) = remember { mutableStateOf("") }
    val activity = LocalActivity.current as Activity
    BackHandler(enabled = true) {
        dispatch(SSODiscoveryFallbackScreenAction.ResetEverything)
    }
    Column {
        BackButton {
            dispatch(SSODiscoveryFallbackScreenAction.ResetEverything)
        }
        PageTitle(textAlign = TextAlign.Left, text = "Sorry, we couldn't find any connections")
        BodyText(
            text =
                "Please input the Organization's unique slug to continue. If you don't know the unique slug, log in " +
                    "through another method to view all of your available Organizations.",
        )
        StytchInput(
            modifier = Modifier.fillMaxWidth(),
            value = slug,
            onValueChange = setSlug,
            label = "Enter org slug",
        )
        state.error?.let {
            FormFieldStatus(isError = true, text = it)
        }
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(
            enabled = slug.isNotBlank(),
            text = "Continue",
            onClick = { dispatch(SSODiscoveryFallbackScreenAction.Submit(activity, slug)) },
        )
        Spacer(modifier = Modifier.height(16.dp))
        BodyText(
            text = "Try another login method",
            modifier =
                Modifier.clickable {
                    dispatch(SSODiscoveryFallbackScreenAction.ResetEverything)
                },
        )
    }
}

package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import kotlinx.coroutines.flow.StateFlow

internal class ErrorScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction)

@Composable
internal fun ErrorScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<ErrorScreenViewModel>,
    viewModel: ErrorScreenViewModel = createViewModel(ErrorScreenViewModel::class.java),
) {
    val b2bError = state.value.b2BErrorType ?: return
    val orgName = state.value.activeOrganization?.organizationName ?: "the organization"
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Only show a back button if it's _not_ organization not found, as all others are potentially user-recoverable
        if (b2bError != B2BErrorType.Organization) {
            BackButton { viewModel.dispatch(ResetEverything) }
        }
        PageTitle(text = "Looks like there was an error!")
        Image(
            painter = painterResource(id = R.drawable.big_error_circle),
            contentDescription = "Error",
        )
        Spacer(modifier = Modifier.height(32.dp))
        BodyText(text = b2bError.description.format(orgName), textAlign = TextAlign.Center)
    }
}

package com.stytch.sdk.ui.b2b.screens.error

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.Dispatch
import com.stytch.sdk.ui.b2b.data.B2BErrorType
import com.stytch.sdk.ui.b2b.data.ResetEverything
import com.stytch.sdk.ui.shared.components.BackButton
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle

@Composable
internal fun ErrorScreen(viewModel: ErrorScreenViewModel) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val b2bError = state.value.b2BErrorType ?: return
    val orgName = state.value.activeOrganization?.organizationName ?: "the organization"
    ErrorScreenComposable(b2bError = b2bError, orgName = orgName, dispatch = viewModel::dispatch)
}

@Composable
internal fun ErrorScreenComposable(
    b2bError: B2BErrorType,
    orgName: String,
    dispatch: Dispatch,
) {
    // Only show a back button if it's _not_ organization not found, as all others are potentially user-recoverable
    val hasBackButton = !listOf(B2BErrorType.Organization, B2BErrorType.NoAuthenticationMethodsFound).contains(b2bError)
    BackHandler(enabled = hasBackButton) {
        dispatch(ResetEverything)
    }
    Column {
        if (hasBackButton) {
            BackButton { dispatch(ResetEverything) }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PageTitle(text = "Looks like there was an error!")
            Image(
                painter = painterResource(id = R.drawable.big_error_circle),
                contentDescription = "Error",
            )
            Spacer(modifier = Modifier.height(32.dp))
            BodyText(text = b2bError.description.format(orgName), textAlign = TextAlign.Center)
        }
    }
}

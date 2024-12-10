package com.stytch.sdk.ui.b2b.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.b2b.usecases.UseTOTPCreate
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class TOTPEnrollmentScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    private val _totpState = MutableStateFlow(state.value.mfaTOTPState)
    val totpState = _totpState.asStateFlow()

    private val useTOTPCreate = UseTOTPCreate(viewModelScope, state, ::dispatch, ::request)

    init {
        if (state.value.mfaTOTPState == null) {
            // kick off account creation
            useTOTPCreate()
        }
    }

    fun goToCodeEntry() {
        dispatch(SetNextRoute(Routes.TOTPEntry))
    }
}

@Composable
internal fun TOTPEnrollmentScreen(
    createViewModel: CreateViewModel<TOTPEnrollmentScreenViewModel>,
    viewModel: TOTPEnrollmentScreenViewModel = createViewModel(TOTPEnrollmentScreenViewModel::class.java),
) {
    val totpState = viewModel.totpState.collectAsState().value
    val theme = LocalStytchTheme.current
    val clipboardManager = LocalClipboardManager.current
    if (totpState == null || totpState.isCreating) {
        return LoadingView(color = Color(theme.inputTextColor))
    }
    if (totpState.error != null) {
        return BodyText(text = totpState.error.message)
    }
    val secret = (totpState.enrollmentState?.secret ?: "").lowercase()
    val secretChunked = secret.chunked(4).joinToString(" ")
    var didCopyCode by remember { mutableStateOf(false) }
    Column {
        PageTitle(text = "Copy the code below to link your authenticator app")
        BodyText(
            text =
                "Enter the key below into your authenticator app. " +
                    "If you don’t have an authenticator app, you’ll need to install one first.",
        )
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
        StytchButton(enabled = didCopyCode, text = "Continue", onClick = viewModel::goToCodeEntry)
    }
}

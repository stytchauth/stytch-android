package com.stytch.sdk.ui.b2b.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stytch.sdk.R
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import kotlinx.coroutines.flow.StateFlow

internal class RecoveryCodesSaveScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
) : BaseViewModel(state, dispatchAction) {
    fun acknowledgeSave() {
        dispatch(SetNextRoute(state.value.postAuthScreen))
    }
}

@Composable
internal fun RecoveryCodesSaveScreen(
    state: State<B2BUIState>,
    createViewModel: CreateViewModel<RecoveryCodesSaveScreenViewModel>,
    viewModel: RecoveryCodesSaveScreenViewModel = createViewModel(RecoveryCodesSaveScreenViewModel::class.java),
) {
    val theme = LocalStytchTheme.current
    val clipboardManager = LocalClipboardManager.current
    val backupCodes =
        state.value.mfaTOTPState
            ?.enrollmentState
            ?.recoveryCodes ?: emptyList()
    val allCodesJoined = backupCodes.joinToString("\n")
    val sendIntent: Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, allCodesJoined)
            type = "text/plain"
        }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current
    Column {
        PageTitle(text = "Save your backup codes!")
        BodyText(text = "This is the only time you will be able to access and save your backup codes.")
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color(theme.disabledButtonBackgroundColor))
                    .border(
                        1.dp,
                        Color(theme.disabledButtonBackgroundColor),
                        RoundedCornerShape(theme.buttonBorderRadius),
                    ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                backupCodes.forEach { code ->
                    Text(
                        modifier = Modifier.fillMaxWidth(0.8f).padding(bottom = 8.dp),
                        text = code,
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
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StytchTextButton(modifier = Modifier.weight(1f), text = "Download codes") {
                context.startActivity(shareIntent)
            }
            StytchTextButton(modifier = Modifier.weight(1f), text = "Copy all") {
                clipboardManager.setText(AnnotatedString(allCodesJoined))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(text = "Done", enabled = true, onClick = viewModel::acknowledgeSave)
    }
}

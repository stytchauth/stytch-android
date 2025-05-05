package com.stytch.sdk.ui.b2b.screens.recoveryCodesSave

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.components.StytchButton
import com.stytch.sdk.ui.shared.components.StytchTextButton
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun RecoveryCodesSaveScreen(viewModel: RecoveryCodesSaveScreenViewModel) {
    val state = viewModel.recoveryCodesSaveState.collectAsState()
    RecoveryCodesSaveScreenComposable(
        backupCodes = state.value.backupCodes,
        dispatch = viewModel::handle,
    )
}

@Composable
private fun RecoveryCodesSaveScreenComposable(
    backupCodes: List<String>,
    dispatch: (RecoveryCodesSaveAction) -> Unit,
) {
    val theme = LocalStytchTheme.current
    val clipboardManager = LocalClipboardManager.current
    val allCodesJoined = backupCodes.joinToString("\n")
    val sendIntent: Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, allCodesJoined)
            type = "text/plain"
        }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current
    var didSaveCodesSomehow by remember { mutableStateOf(false) }
    Column {
        PageTitle(textAlign = TextAlign.Left, text = stringResource(R.string.stytch_b2b_save_your_backup_codes))
        BodyText(
            text =
                stringResource(
                    R.string.stytch_b2b_this_is_the_only_time_you_will_be_able_to_access_and_save_your_backup_codes,
                ),
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
                    ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                backupCodes.forEach { code ->
                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
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
            StytchTextButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.stytch_b2b_download_codes),
            ) {
                context.startActivity(shareIntent)
                didSaveCodesSomehow = true
            }
            StytchTextButton(modifier = Modifier.weight(1f), text = stringResource(R.string.stytch_b2b_copy_all)) {
                clipboardManager.setText(AnnotatedString(allCodesJoined))
                didSaveCodesSomehow = true
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        StytchButton(text = stringResource(R.string.stytch_b2b_done), enabled = didSaveCodesSomehow, onClick = {
            dispatch(RecoveryCodesSaveAction.AcknowledgeSave)
        })
    }
}

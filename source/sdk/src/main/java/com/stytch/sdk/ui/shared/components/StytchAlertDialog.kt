package com.stytch.sdk.ui.shared.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stytch.sdk.R
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StytchAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    body: AnnotatedString,
    cancelText: String? = null,
    onCancelClick: () -> Unit = {},
    acceptText: String,
    onAcceptClick: () -> Unit,
) {
    val type = LocalStytchTypography.current
    val theme = LocalStytchTheme.current
    val semantics = stringResource(id = R.string.semantics_alert_dialog)
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.semantics { contentDescription = semantics },
    ) {
        Surface(
            modifier =
                Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
            shape = RoundedCornerShape(size = 28.dp),
            tonalElevation = AlertDialogDefaults.TonalElevation,
            color = Color.White,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                PageTitle(
                    text = title,
                    textAlign = TextAlign.Start,
                    color = Color(theme.dialogTextColor),
                )
                Body2Text(
                    text = body,
                    color = Color(theme.dialogTextColor),
                )
                Row(
                    modifier = Modifier.align(Alignment.End),
                ) {
                    if (cancelText != null) {
                        TextButton(onClick = onCancelClick) {
                            Text(
                                text = cancelText,
                                style =
                                    type.body2.copy(
                                        color = Color(theme.dialogTextColor),
                                    ),
                            )
                        }
                    }
                    TextButton(onClick = onAcceptClick) {
                        Text(
                            text = acceptText,
                            style =
                                type.body2.copy(
                                    color = Color(theme.dialogTextColor),
                                ),
                        )
                    }
                }
            }
        }
    }
}

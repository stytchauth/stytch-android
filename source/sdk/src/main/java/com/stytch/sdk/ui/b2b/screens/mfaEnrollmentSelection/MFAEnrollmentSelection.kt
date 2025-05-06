package com.stytch.sdk.ui.b2b.screens.mfaEnrollmentSelection

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.R
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.ui.b2b.components.MenuItemWithRightArrow
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme

@Composable
internal fun MFAEnrollmentSelectionScreen(viewModel: MFAEnrollmentSelectionScreenViewModel) {
    val sortedOptions = viewModel.sortedMfaMethods.collectAsStateWithLifecycle()
    MFAEnrollmentSelectionScreenComposable(
        sortedOptions = sortedOptions.value,
        dispatch = viewModel::selectMfaMethod,
    )
}

@Composable
private fun MFAEnrollmentSelectionScreenComposable(
    sortedOptions: List<MfaMethod>,
    dispatch: (MfaMethod) -> Unit,
) {
    val theme = LocalStytchTheme.current
    Column {
        PageTitle(
            textAlign = TextAlign.Left,
            text =
                stringResource(
                    R.string.stytch_b2b_set_up_multi_factor_authentication,
                ),
        )
        BodyText(
            text = stringResource(R.string.stytch_b2b_your_organization_requires_additional),
        )
        Column {
            sortedOptions.forEach { mfaMethod ->
                when (mfaMethod) {
                    MfaMethod.TOTP -> stringResource(R.string.stytch_b2b_use_an_authenticator_app)
                    MfaMethod.SMS -> stringResource(R.string.stytch_b2b_text_me_a_code)
                    MfaMethod.NONE -> null
                }?.let { title ->
                    MenuItemWithRightArrow(title = title, onClick = { dispatch(mfaMethod) })
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(theme.inputBorderColor),
                    )
                }
            }
        }
    }
}

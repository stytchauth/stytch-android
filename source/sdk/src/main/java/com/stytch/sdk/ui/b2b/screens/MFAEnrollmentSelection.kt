package com.stytch.sdk.ui.b2b.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stytch.sdk.b2b.network.models.MfaMethod
import com.stytch.sdk.ui.b2b.BaseViewModel
import com.stytch.sdk.ui.b2b.CreateViewModel
import com.stytch.sdk.ui.b2b.data.B2BUIAction
import com.stytch.sdk.ui.b2b.data.B2BUIState
import com.stytch.sdk.ui.b2b.data.SetNextRoute
import com.stytch.sdk.ui.b2b.data.StytchB2BProductConfig
import com.stytch.sdk.ui.b2b.domain.ALL_MFA_METHODS
import com.stytch.sdk.ui.b2b.domain.getEnabledMethods
import com.stytch.sdk.ui.b2b.navigation.Routes
import com.stytch.sdk.ui.shared.components.BodyText
import com.stytch.sdk.ui.shared.components.PageTitle
import com.stytch.sdk.ui.shared.theme.LocalStytchTheme
import com.stytch.sdk.ui.shared.theme.LocalStytchTypography
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class MFAEnrollmentSelectionScreenViewModel(
    internal val state: StateFlow<B2BUIState>,
    dispatchAction: suspend (B2BUIAction) -> Unit,
    productConfig: StytchB2BProductConfig,
) : BaseViewModel(state, dispatchAction) {
    private val _sortedMfaMethods = MutableStateFlow<List<MfaMethod>>(emptyList())
    val sortedMfaMethods = _sortedMfaMethods.asStateFlow()

    init {
        val mfaProductInclude = productConfig.mfaProductInclude
        val mfaProductOrder =
            productConfig.mfaProductOrder.toSet().ifEmpty { ALL_MFA_METHODS }
        val organizationMfaOptionsSupported =
            state.value.mfaPrimaryInfoState?.organizationMfaOptionsSupported ?: emptyList()
        val optionsToDisplay =
            getEnabledMethods(
                orgSupportedMethods = organizationMfaOptionsSupported,
                uiIncludedMfaMethods = mfaProductInclude,
            )
        _sortedMfaMethods.value =
            sortMfaMethodsIntoCorrectOrder(
                allOptions = optionsToDisplay,
                expectedOrdering = mfaProductOrder,
            )
    }

    fun selectMfaMethod(mfaMethod: MfaMethod) {
        when (mfaMethod) {
            MfaMethod.SMS -> dispatch(SetNextRoute(Routes.SMSOTPEnrollment))
            MfaMethod.TOTP -> dispatch(SetNextRoute(Routes.TOTPEnrollment))
            MfaMethod.NONE -> { /* noop */ }
        }
    }

    private fun sortMfaMethodsIntoCorrectOrder(
        allOptions: Set<MfaMethod>,
        expectedOrdering: Set<MfaMethod>,
    ): List<MfaMethod> {
        val sortedOptions = mutableSetOf<MfaMethod>()
        expectedOrdering.forEach { mfaMethod ->
            if (allOptions.contains(mfaMethod)) {
                sortedOptions.add(mfaMethod)
            }
        }
        // append remaining options
        allOptions.forEach { mfaMethod -> sortedOptions.add(mfaMethod) }
        return sortedOptions.toList()
    }
}

@Composable
internal fun MFAEnrollmentSelectionScreen(
    createViewModel: CreateViewModel<MFAEnrollmentSelectionScreenViewModel>,
    viewModel: MFAEnrollmentSelectionScreenViewModel =
        createViewModel(MFAEnrollmentSelectionScreenViewModel::class.java),
) {
    val sortedOptions = viewModel.sortedMfaMethods.collectAsStateWithLifecycle()
    val theme = LocalStytchTheme.current
    Column {
        PageTitle(text = "Set up Multi-Factor Authentication")
        BodyText(
            text = "Your organization requires an additional form of verification to make your account more secure.",
        )
        Column {
            sortedOptions.value.forEach { mfaMethod ->
                when (mfaMethod) {
                    MfaMethod.TOTP -> "Use an authenticator app"
                    MfaMethod.SMS -> "Text me a code"
                    MfaMethod.NONE -> null
                }?.let { title ->
                    MfaMethodSelector(title = title, onClick = { viewModel.selectMfaMethod(mfaMethod) })
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(theme.inputBorderColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun MfaMethodSelector(
    title: String,
    onClick: () -> Unit,
) {
    val theme = LocalStytchTheme.current
    val type = LocalStytchTypography.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style =
                type.body.copy(
                    color = Color(theme.primaryTextColor),
                    fontWeight = FontWeight.Bold,
                ),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(theme.primaryTextColor),
        )
    }
}

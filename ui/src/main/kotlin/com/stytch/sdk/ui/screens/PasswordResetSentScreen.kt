package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.data.PasswordResetDetails
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PasswordResetSentScreen(
    val details: PasswordResetDetails
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<PasswordResetScreenViewModel>()
        PasswordResetSentScreenComposable(
            details = details,
            viewModel = viewModel
        )
    }
}

@Composable
private fun PasswordResetSentScreenComposable(
    details: PasswordResetDetails,
    viewModel: PasswordResetScreenViewModel,
) {
    Text(text = "Password reset sent")
}

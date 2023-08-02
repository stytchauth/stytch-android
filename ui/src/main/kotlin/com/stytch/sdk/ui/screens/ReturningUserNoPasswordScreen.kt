package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.data.StytchProductConfig
import kotlinx.parcelize.Parcelize

@Parcelize
internal class ReturningUserNoPasswordScreen(
    val emailAddress: String,
    val productConfig: StytchProductConfig,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<ReturningUserNoPasswordScreenViewModel>()
        ReturningUserNoPasswordScreenComposable(
            emailAddress = emailAddress,
            productConfig = productConfig,
            viewModel = viewModel
        )
    }
}

@Composable
private fun ReturningUserNoPasswordScreenComposable(
    emailAddress: String,
    productConfig: StytchProductConfig,
    viewModel: ReturningUserNoPasswordScreenViewModel
) {
}

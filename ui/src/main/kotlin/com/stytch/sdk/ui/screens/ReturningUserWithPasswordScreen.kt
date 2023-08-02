package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.data.StytchProductConfig
import kotlinx.parcelize.Parcelize

@Parcelize
internal class ReturningUserWithPasswordScreen(
    val emailAddress: String,
    val productConfig: StytchProductConfig,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<ReturningUserWithPasswordScreenViewModel>()
        ReturningUserWithPasswordScreenComposable(
            emailAddress = emailAddress,
            productConfig = productConfig,
            viewModel = viewModel
        )
    }
}

@Composable
private fun ReturningUserWithPasswordScreenComposable(
    emailAddress: String,
    productConfig: StytchProductConfig,
    viewModel: ReturningUserWithPasswordScreenViewModel
) {
}

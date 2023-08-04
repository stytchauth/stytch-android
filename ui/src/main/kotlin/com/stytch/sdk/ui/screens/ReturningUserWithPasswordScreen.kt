package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.theme.LocalStytchProductConfig
import kotlinx.parcelize.Parcelize

@Parcelize
internal class ReturningUserWithPasswordScreen(
    val emailAddress: String,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<ReturningUserWithPasswordScreenViewModel>()
        val productConfig = LocalStytchProductConfig.current
        ReturningUserWithPasswordScreenComposable(
            emailAddress = emailAddress
        )
    }
}

@Composable
private fun ReturningUserWithPasswordScreenComposable(
    emailAddress: String,
) {

}

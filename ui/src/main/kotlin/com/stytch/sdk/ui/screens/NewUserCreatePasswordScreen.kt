package com.stytch.sdk.ui.screens

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import com.stytch.sdk.ui.data.StytchProductConfig
import kotlinx.parcelize.Parcelize

@Parcelize
internal class NewUserCreatePasswordScreen(
    val emailAddress: String,
    val productConfig: StytchProductConfig,
) : AndroidScreen(), Parcelable {
    @Composable
    override fun Content() {
        val viewModel = viewModel<NewUserCreatePasswordScreenViewModel>()
        NewUserCreatePasswordScreenComposable(
            emailAddress = emailAddress,
            productConfig = productConfig,
            viewModel = viewModel
        )
    }
}

@Composable
private fun NewUserCreatePasswordScreenComposable(
    emailAddress: String,
    productConfig: StytchProductConfig,
    viewModel: NewUserCreatePasswordScreenViewModel
) {
}

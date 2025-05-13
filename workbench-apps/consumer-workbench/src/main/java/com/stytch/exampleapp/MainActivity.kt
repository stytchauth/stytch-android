package com.stytch.exampleapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.theme.AppTheme
import com.stytch.exampleapp.ui.ConsumerWorkbenchApp
import com.stytch.exampleapp.ui.ConsumerWorkbenchAppViewModel
import com.stytch.sdk.consumer.StytchClient
import kotlin.getValue

class MainActivity : FragmentActivity() {
    val viewModel by viewModels<ConsumerWorkbenchAppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        StytchClient.oauth.setOAuthReceiverActivity(this)
        super.onCreate(savedInstanceState)
        setContent {
            val state = viewModel.uiState.collectAsState()
            AppTheme {
                ConsumerWorkbenchApp(state.value)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StytchClient.oauth.setOAuthReceiverActivity(null)
    }
}

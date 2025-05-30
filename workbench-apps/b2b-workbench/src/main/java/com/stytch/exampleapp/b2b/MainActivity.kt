package com.stytch.exampleapp.b2b

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.b2b.theme.AppTheme
import com.stytch.exampleapp.b2b.ui.B2BWorkbenchApp
import com.stytch.exampleapp.b2b.ui.B2BWorkbenchAppViewModel
import com.stytch.sdk.b2b.StytchB2BClient

class MainActivity : FragmentActivity() {
    val viewModel by viewModels<B2BWorkbenchAppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        StytchB2BClient.oauth.setOAuthReceiverActivity(this)
        StytchB2BClient.sso.setSSOReceiverActivity(this)
        super.onCreate(savedInstanceState)
        intent.data?.let {
            viewModel.handleDeeplink(it)
        }
        setContent {
            val state = viewModel.uiState.collectAsState()
            AppTheme {
                B2BWorkbenchApp(
                    state = state.value,
                    logout = viewModel::logout,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StytchB2BClient.oauth.setOAuthReceiverActivity(null)
        StytchB2BClient.sso.setSSOReceiverActivity(null)
    }
}

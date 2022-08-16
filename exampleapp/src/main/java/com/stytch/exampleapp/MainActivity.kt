package com.stytch.exampleapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.stytch.exampleapp.theme.AppTheme
import com.stytch.exampleapp.ui.ExampleAppScreen

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ExampleAppScreen()
            }
        }
        if (intent.action == Intent.ACTION_VIEW) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let { appLinkData ->
            viewModel.authenticate(appLinkData)
            viewModel.handleUri(appLinkData)
        }
    }

}

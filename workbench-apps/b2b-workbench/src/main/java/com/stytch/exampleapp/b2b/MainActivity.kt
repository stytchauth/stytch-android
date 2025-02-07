package com.stytch.exampleapp.b2b

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.stytch.exampleapp.b2b.theme.AppTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Column(
                    modifier =
                        Modifier
                            .padding(64.dp)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                ) {
                    Button(onClick = ::launchHeadlessWorkbench) {
                        Text(text = "Launch Headless Workbench")
                    }
                    Button(onClick = ::launchUIWorkbench) {
                        Text(text = "Launch UI Workbench")
                    }
                }
            }
        }
    }

    private fun launchHeadlessWorkbench() {
        val intent = Intent(this, HeadlessWorkbenchActivity::class.java)
        startActivity(intent)
    }

    private fun launchUIWorkbench() {
        val intent = Intent(this, UIWorkbenchActivity::class.java)
        startActivity(intent)
    }
}

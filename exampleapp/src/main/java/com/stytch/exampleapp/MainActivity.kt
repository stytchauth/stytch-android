package com.stytch.exampleapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.stytch.exampleapp.theme.AppTheme
import com.stytch.exampleapp.ui.ExampleAppScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ExampleAppScreen()
            }
        }
    }
}

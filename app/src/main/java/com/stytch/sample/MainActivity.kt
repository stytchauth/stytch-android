package com.stytch.sample

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.stytch.sdk.StytchConfig
import com.stytch.sdk.StytchSDK
import com.stytch.sdk.helpers.LoggerLocal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun launchStytch(view: View) {
        StytchSDK().launchFlow(this)
    }

    companion object{
        private const val TAG = "MainActivity"
    }
}
package com.stytch.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.toggle_activity_button).apply {
            text = "Switch to Java Activity"
            setOnClickListener { switchToJavaActivity() }
        }

        findViewById<Button>(R.id.toggle_dark_mode_button).setOnClickListener {
            toggleDarkMode()
        }

        findViewById<Button>(R.id.magic_link_direct_api_button).setOnClickListener {
            testMagicLinkDirectApi()
        }

        findViewById<Button>(R.id.magic_link_ui_flow_button).setOnClickListener {
            testEmailMagicLinkUIFlow()
        }

        findViewById<Button>(R.id.sms_passcode_ui_flow_button).setOnClickListener {
            testSMSPasscodeUIFlow()
        }

        resultTextView = findViewById(R.id.result_text_view)
    }

    private fun switchToJavaActivity() {
    }

    private fun toggleDarkMode() {
        AppCompatDelegate.setDefaultNightMode(
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun showResult(result: Any) {
        val asString = result.toString()
        GlobalScope.launch(Dispatchers.Main) {
            resultTextView.text = asString
            showToast(asString)
            Timber.tag("StytchTestApp").i("Result received: $asString")
        }
    }

    private fun testMagicLinkDirectApi() {

    }

    private fun testEmailMagicLinkUIFlow() {
    }

    private fun testSMSPasscodeUIFlow() {

    }
}

package com.stytch.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchEnvironment
import com.stytch.sdk.StytchUI
import com.stytch.sdk.registerStytchEmailMagicLinkActivity
import com.stytch.sdk.registerStytchSMSPasscodeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    lateinit var resultTextView: TextView

    val stytchEmailMagicLinkActivityLauncher = registerStytchEmailMagicLinkActivity {
        showResult(it)
    }

    val stytchSMSPasscodeActivityLauncher = registerStytchSMSPasscodeActivity {
        showResult(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stytch.configure(
            projectId = "project-test-2328d332-7850-486a-99c6-cd7956b518c2",
            secret = "secret-test-2tbeMPP3cYW-jXbpt-Ag1yeYDUW4ReEBxHw=",
            environment = StytchEnvironment.TEST,
        )

        findViewById<Button>(R.id.toggle_dark_mode_button).setOnClickListener {
            toggleDarkMode()
        }

        findViewById<Button>(R.id.magic_link_direct_api_button).setOnClickListener {
            testMagicLinkDirectApi()
        }

        findViewById<Button>(R.id.magic_link_ui_flow_button).setOnClickListener {
            testMagicLinkUIFlow()
        }

        findViewById<Button>(R.id.sms_passcode_ui_flow_button).setOnClickListener {
            testSMSPasscodeUIFlow()
        }

        resultTextView = findViewById(R.id.result_text_view)
    }

    private fun showResult(result: Any) {
        val theResult = result.toString()
        GlobalScope.launch(Dispatchers.Main) {
            resultTextView.text = theResult
            showToast(theResult)
            Timber.tag("StytchTestApp").i("Result received: $theResult")
        }
    }

    private fun toggleDarkMode() {
        AppCompatDelegate.setDefaultNightMode(
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun testMagicLinkDirectApi() {
        GlobalScope.launch {
            val result = StytchApi.MagicLinks.sendMagicLinkByEmail(
                email = "kyle@stytch.com",
                magicLinkUrl = "https://test.stytch.com/login",
            )
            showResult(result)
        }
    }

    private fun testMagicLinkUIFlow() {
        val configuration = StytchUI.EmailMagicLink.Configuration(
            loginMagicLinkUrl = "https://test.stytch.com/login",
            signupMagicLinkUrl = "https://test.stytch.com/signup",
            createUserAsPending = true,
        )
        stytchEmailMagicLinkActivityLauncher.launch(configuration)
    }

    private fun testSMSPasscodeUIFlow() {
        val configuration = StytchUI.SMSPasscode.Configuration(
            createUserAsPending = true,
            hashStringSet = false,
        )
        stytchSMSPasscodeActivityLauncher.launch(configuration)
    }
}

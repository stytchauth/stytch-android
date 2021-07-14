package com.stytch.testapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchEnvironment
import com.stytch.sdk.StytchUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stytch.configure(
            publicToken = "public-token-test-6906b71d-b14d-403c-becc-c4c88ac5fcfa",
            environment = StytchEnvironment.TEST,
        )

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
        startActivity(Intent(this, JavaActivity::class.java))
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
        GlobalScope.launch {
            val result = StytchApi.MagicLinks.Email.loginOrCreate(
                email = "kyle@stytch.com",
                loginMagicLinkUrl = "https://test.stytch.com/login",
                signupMagicLinkUrl = "https://test.stytch.com/signup",
            )
            showResult(result)
        }
    }

    private fun testEmailMagicLinkUIFlow() {
        StytchUI.EmailMagicLink.configure(
            loginMagicLinkUrl = "https://test.stytch.com/login",
            signupMagicLinkUrl = "https://test.stytch.com/signup",
            createUserAsPending = true,
            authenticator = { token ->
                showResult("Received token '$token'")
                StytchUI.onTokenAuthenticated(success = false)
            }
        )
        val intent = StytchUI.EmailMagicLink.createIntent(this)
        startActivity(intent)
    }

    private fun testSMSPasscodeUIFlow() {
        StytchUI.SMSPasscode.configure(
            createUserAsPending = true,
            authenticator = { methodId, token ->
                showResult("Received methodId '$methodId' and token '$token'")
                StytchUI.onTokenAuthenticated(success = false)
            }
        )
        val intent = StytchUI.SMSPasscode.createIntent(this)
        startActivity(intent)
    }
}

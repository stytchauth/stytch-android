package com.stytch.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.compose.setContent
import com.stytch.testapp.theme.TestAppTheme
import com.stytch.testapp.ui.TestAppScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestAppTheme {
                TestAppScreen()
            }
        }
    }

//    private fun toggleDarkMode() {
//        AppCompatDelegate.setDefaultNightMode(
//            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) AppCompatDelegate.MODE_NIGHT_YES
//            else AppCompatDelegate.MODE_NIGHT_NO
//        )
//    }
//
//    private fun showResult(result: Any) {
//        val asString = result.toString()
//        GlobalScope.launch(Dispatchers.Main) {
//            resultTextView.text = asString
//            showToast(asString)
//            Timber.tag("StytchTestApp").i("Result received: $asString")
//        }
//    }
//
//    private fun testMagicLinkDirectApi() {
//        GlobalScope.launch {
//            val result = StytchClient.MagicLinks.loginOrCreate(
//                email = "kyle@stytch.com",
//                loginMagicLinkUrl = "https://test.stytch.com/login",
//                signupMagicLinkUrl = "https://test.stytch.com/signup",
//            )
//            showResult(result)
//        }
//    }
//
//    private fun testEmailMagicLinkUIFlow() {
//        StytchUI.EmailMagicLink.configure(
//            loginMagicLinkUrl = "https://test.stytch.com/login",
//            signupMagicLinkUrl = "https://test.stytch.com/signup",
//            createUserAsPending = true,
//            authenticator = { token ->
//                showResult("Received token '$token'")
//                StytchUI.onTokenAuthenticated()
//            }
//        )
//        val intent = StytchUI.EmailMagicLink.createIntent(this)
//        startActivity(intent)
//    }
//
//    private fun testSMSPasscodeUIFlow() {
//        StytchUI.SMSPasscode.configure(
//            createUserAsPending = true,
//            authenticator = { methodId, token ->
//                showResult("Received methodId '$methodId' and token '$token'")
//                StytchUI.onTokenAuthenticated()
//            }
//        )
//        val intent = StytchUI.SMSPasscode.createIntent(this)
//        startActivity(intent)
//    }
}

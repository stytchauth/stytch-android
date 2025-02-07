package com.stytch.exampleapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.text.input.TextFieldValue
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.stytch.exampleapp.theme.AppTheme
import com.stytch.exampleapp.ui.AppScreen
import com.stytch.sdk.consumer.StytchClient

private const val SMS_CONSENT_REQUEST = 2
const val THIRD_PARTY_OAUTH_REQUEST = 4

class MainActivity : FragmentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val oauthViewModel: OAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        StytchClient.oauth.setOAuthReceiverActivity(this)
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppScreen(homeViewModel, oauthViewModel)
            }
        }
        if (intent.action == Intent.ACTION_VIEW) {
            handleIntent(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StytchClient.oauth.setOAuthReceiverActivity(null)
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let { appLinkData ->
            Toast.makeText(this, getString(R.string.deeplink_received_toast), Toast.LENGTH_LONG).show()
            homeViewModel.handleUri(appLinkData)
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    homeViewModel.otpTokenTextState = TextFieldValue(message?.substringAfterLast(" ") ?: "")
                } else {
                    // Consent denied. User can type OTC manually.
                }
            THIRD_PARTY_OAUTH_REQUEST -> data?.let { oauthViewModel.authenticateThirdPartyOAuth(resultCode, it) }
        }
    }
}

package com.stytch.exampleapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.text.input.TextFieldValue
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.stytch.exampleapp.theme.AppTheme
import com.stytch.exampleapp.ui.AppScreen

private const val SMS_CONSENT_REQUEST = 2
const val GOOGLE_OAUTH_REQUEST = 3
class MainActivity : FragmentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val oauthViewModel: OAuthViewModel by viewModels()

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        try {
                            // Get consent intent and start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)?.let {
                                startActivityForResult(it, SMS_CONSENT_REQUEST)
                            }
                        } catch (e: ActivityNotFoundException) {
                            // Handle the exception ...
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, do nothing
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppScreen(homeViewModel, oauthViewModel)
            }
        }
        if (intent.action == Intent.ACTION_VIEW) {
            handleIntent(intent)
        }
        // Supported since version O
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            SmsRetriever.getClient(baseContext).startSmsUserConsent(null)
            registerReceiver(smsVerificationReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }
    }

    private fun handleIntent(intent: Intent) {
        intent.data?.let { appLinkData ->
            Toast.makeText(this, getString(R.string.deeplink_received_toast), Toast.LENGTH_LONG).show()
            homeViewModel.handleUri(appLinkData)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    homeViewModel.otpTokenTextState = TextFieldValue(message?.substringAfterLast(" ") ?: "")
                } else {
                    // Consent denied. User can type OTC manually.
                }
            GOOGLE_OAUTH_REQUEST -> data?.let { oauthViewModel.authenticateGoogleOneTapLogin(it) }
        }
    }
}

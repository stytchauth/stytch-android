package com.stytch.sdk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.stytch.sdk.screens.EmailMagicLinkHomeScreen
import com.stytch.sdk.screens.SMSPasscodeEnterPasscodeScreen
import com.stytch.sdk.screens.SMSPasscodeHomeScreen
import com.wealthfront.magellan.Navigator

internal abstract class StytchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityId = intent.getIntExtra(ACTIVITY_ID_EXTRA_NAME, -1)
        if (navigator == null || activityId != currentActivityId) {
            currentActivityId = activityId
            navigator = createNavigator()
        }
        setContentView(R.layout.stytch_activity_layout)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        actionBar?.customize()
        supportActionBar?.customize()
    }

    protected abstract fun createNavigator(): Navigator

    private fun android.app.ActionBar.customize() {
        with(StytchUI.uiCustomization) {
            if (hideActionBar) hide()
            setBackgroundDrawable(
                GradientDrawable().apply {
                    setColor(actionBarColor.getColor(this@StytchActivity))
                }
            )
        }
    }

    private fun ActionBar.customize() {
        with(StytchUI.uiCustomization) {
            if (hideActionBar) hide()
            setBackgroundDrawable(
                GradientDrawable().apply {
                    setColor(actionBarColor.getColor(this@StytchActivity))
                }
            )
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        navigator?.onCreate(this, savedInstanceState)
        super.onPostCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigator?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        navigator?.onResume(this)
    }

    override fun onPause() {
        navigator?.onPause(this)
        super.onPause()
    }

    override fun onDestroy() {
        navigator?.onDestroy(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (navigator?.handleBack() != true) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        navigator?.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        navigator?.onPrepareOptionsMenu(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    companion object {
        // The navigator clears references to context objects when the activity is destroyed
        // See StytchActivity::onDestroy
        @SuppressLint("StaticFieldLeak")
        var navigator: Navigator? = null

        private var currentActivityId = -1
        private var nextActivityId = 0

        fun getNextActivityId(): Int {
            val returnVal = nextActivityId
            nextActivityId++
            return returnVal
        }

        const val ACTIVITY_ID_EXTRA_NAME = "activity_id"
    }
}

internal enum class IntentCodes {
    EMAIL_PICKER_INTENT_CODE,
    PHONE_NUMBER_PICKER_INTENT_CODE,
    SMS_ONE_TAP_AUTOFILL_INTENT_CODE,
    SMS_ZERO_TAP_AUTOFILL_INTENT_CODE,
}

internal class StytchEmailMagicLinkActivity : StytchActivity() {

    override fun createNavigator(): Navigator {
        if (!StytchUI.EmailMagicLink.configured) {
            stytchError("Launched StytchUI Email Magic Link activity with first calling StytchUI.EmailMagicLink.configure(...)")
        }
        return Navigator.withRoot(EmailMagicLinkHomeScreen()).build()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val token = intent?.data?.getQueryParameter("token")
        if (token != null) {
            StytchUI.EmailMagicLink.authenticator.authenticateToken(token)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentCodes.EMAIL_PICKER_INTENT_CODE.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                val emailAddress = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.id
                emailAddress?.let {
                    (navigator?.currentScreen() as? EmailMagicLinkHomeScreen)?.emailAddressHintGiven(it)
                }
            }
        }
    }
}

internal class StytchSMSPasscodeActivity : StytchActivity() {
    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            runCatching {
                if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
                    val extras = intent.extras
                    if ((extras?.get(SmsRetriever.EXTRA_STATUS) as? Status)?.statusCode == CommonStatusCodes.SUCCESS) {
                        val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        startActivityForResult(consentIntent, IntentCodes.SMS_ONE_TAP_AUTOFILL_INTENT_CODE.ordinal)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsVerificationReceiver, intentFilter)
    }

    override fun createNavigator(): Navigator {
        if (!StytchUI.SMSPasscode.configured) {
            stytchError("Launched StytchUI SMS Passcode activity with first calling StytchUI.SMSPasscode.configure(...)")
        }
        return Navigator.withRoot(SMSPasscodeHomeScreen()).build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentCodes.PHONE_NUMBER_PICKER_INTENT_CODE.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                val phoneNumber = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.id
                phoneNumber?.let {
                    (navigator?.currentScreen() as? SMSPasscodeHomeScreen)?.phoneNumberHintGiven(it)
                }
            }
            IntentCodes.SMS_ONE_TAP_AUTOFILL_INTENT_CODE.ordinal -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                val smsMessage = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                smsMessage?.let {
                    (navigator?.currentScreen() as? SMSPasscodeEnterPasscodeScreen)?.smsReceived(it)
                }
            }
        }
    }
}

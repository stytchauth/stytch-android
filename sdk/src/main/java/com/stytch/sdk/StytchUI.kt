package com.stytch.sdk

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.wealthfront.magellan.Navigator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

public object StytchUI {
    private var _uiCustomization: StytchUICustomization? = null
    public var uiCustomization: StytchUICustomization
        set(value) {
            _uiCustomization = value
        }
        get() {
            if (_uiCustomization == null) uiCustomization = StytchUICustomization()
            return _uiCustomization!!
        }

    public object EmailMagicLink {
        internal var configured = false
        internal lateinit var loginMagicLinkUrl: String
        internal lateinit var signupMagicLinkUrl: String
        internal var createUserAsPending = false

        public fun configure(
            loginMagicLinkUrl: String,
            signupMagicLinkUrl: String,
            createUserAsPending: Boolean,
        ) {
            configured = true
            this.loginMagicLinkUrl = loginMagicLinkUrl
            this.signupMagicLinkUrl = signupMagicLinkUrl
            this.createUserAsPending = createUserAsPending
        }

        public fun activityLauncher(
            context: ActivityResultCaller,
            onResult: (StytchUIResult) -> Unit,
        ): ActivityResultLauncher<Unit> {
            return context.registerForActivityResult(activityResultContract, onResult)
        }

        internal val activityResultContract by lazy {
            object : ActivityResultContract<Unit, StytchUIResult>() {
                override fun createIntent(context: Context, input: Unit): Intent {
                    return this@EmailMagicLink.createIntent(context)
                }

                override fun parseResult(resultCode: Int, intent: Intent?): StytchUIResult {
                    return intent.toStytchUIResult()
                }
            }
        }

        public fun createIntent(appContext: Context): Intent {
            return stytchIntent(appContext, StytchEmailMagicLinkActivity::class.java)
        }
    }

    public object SMSPasscode {
        internal var configured = false
        internal var hashStringSet = false
        internal var createUserAsPending = false

        public fun configure(
            hashStringSet: Boolean,
            createUserAsPending: Boolean,
        ) {
            configured = true
            this.hashStringSet = hashStringSet
            this.createUserAsPending = createUserAsPending
        }

        public fun activityLauncher(
            context: ActivityResultCaller,
            onResult: (StytchUIResult) -> Unit,
        ): ActivityResultLauncher<Unit> {
            return context.registerForActivityResult(activityResultContract, onResult)
        }

        internal val activityResultContract by lazy {
            object : ActivityResultContract<Unit, StytchUIResult>() {
                override fun createIntent(context: Context, input: Unit): Intent {
                    return this@SMSPasscode.createIntent(context)
                }

                override fun parseResult(resultCode: Int, intent: Intent?): StytchUIResult {
                    return intent.toStytchUIResult()
                }
            }
        }

        public fun createIntent(appContext: Context): Intent {
            return stytchIntent(appContext, StytchSMSPasscodeActivity::class.java)
        }
    }
}

internal class StytchEmailMagicLinkActivity : StytchActivity() {

    override fun createNavigator(): Navigator {
        if (!StytchUI.EmailMagicLink.configured) {
            error("Stytch Error: Launch StytchUI Email Magic Link activity with first calling StytchUI.EmailMagicLink.configure(...)")
        }
        return Navigator.withRoot(EmailMagicLinkHomeScreen()).build()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val token = intent?.data?.getQueryParameter("token")
        if (token != null) {
            // Verify deeplink
            GlobalScope.launch {
                val result = StytchApi.MagicLinks.authenticateMagicLink(token)
                when (result) {
                    is StytchResult.Success -> {
                        Log.d("StytchLog", "Successful Magic Link Authentication")
                        setResult(RESULT_OK, intentWithExtra(result.value))
                        finish()
                    }
                    is StytchResult.Error   -> {
                        Log.d("StytchLog", "Failed Magic Link Authentication")
                    }
                    StytchResult.NetworkError -> TODO()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentCodes.EMAIL_PICKER_INTENT_CODE.ordinal -> {
                if (resultCode != Activity.RESULT_OK) return
                val emailAddress = data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.id
                Log.d("StytchLog", "$emailAddress")
                emailAddress?.let {
                    (navigator?.currentScreen() as? EmailMagicLinkHomeScreen)?.emailAddressHintGiven(it)
                }
            }
        }
    }
}

public class StytchSMSPasscodeActivity : StytchActivity() {
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
            error("Stytch Error: Launch StytchUI SMS Passcode activity with first calling StytchUI.SMSPasscode.configure(...)")
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

internal enum class IntentCodes {
    EMAIL_PICKER_INTENT_CODE,
    PHONE_NUMBER_PICKER_INTENT_CODE,
    SMS_ONE_TAP_AUTOFILL_INTENT_CODE,
    SMS_ZERO_TAP_AUTOFILL_INTENT_CODE,
}

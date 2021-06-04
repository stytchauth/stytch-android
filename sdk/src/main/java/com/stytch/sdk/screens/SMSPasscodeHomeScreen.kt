package com.stytch.sdk.screens

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.stytch.sdk.IntentCodes
import com.stytch.sdk.R
import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchEditText
import com.stytch.sdk.StytchErrorTextView
import com.stytch.sdk.StytchResult
import com.stytch.sdk.StytchScreen
import com.stytch.sdk.StytchScreenView
import com.stytch.sdk.StytchUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SMSPasscodeHomeScreen : StytchScreen<SMSPasscodeHomeView>() {
    var phoneHintPickerShown = false
    var isInErrorState = MutableStateFlow(false)
    var errorMessage = MutableStateFlow<@StringRes Int>(R.string.unknown_error)
    var isPhoneNumberTextFieldInErrorState = MutableStateFlow(false)
    var buttonText = MutableStateFlow<@StringRes Int>(R.string._continue)
    var isButtonEnabled = MutableStateFlow(false)
    var phoneNumberTextFieldText = ""

    override fun createView(context: Context): SMSPasscodeHomeView {
        return SMSPasscodeHomeView(context).apply {
            setBackgroundColor(StytchUI.uiCustomization.backgroundColor.getColor(context))

            phoneNumberTextField.addTextChangedListener {
                val asString = it.toString()
                if (asString != phoneNumberTextFieldText) {
                    phoneNumberTextFieldText = asString
                    isButtonEnabled.value = !view.phoneNumberTextField.text.isNullOrEmpty()
                    this@SMSPasscodeHomeScreen.isInErrorState.value = false
                    isPhoneNumberTextFieldInErrorState.value = false
                }
            }

            continueButton.setOnClickListener { onContinueButtonClicked() }
        }
    }

    private fun onContinueButtonClicked() {
        val enteredPhoneNumber = view.phoneNumberTextField.text.toString()

        isButtonEnabled.value = false
        buttonText.value = R.string.sending_passcode

        GlobalScope.launch(Dispatchers.IO) {
            val result = StytchApi.OTP.loginOrCreateUserBySMS(
                phoneNumber = "+1$enteredPhoneNumber",
                createUserAsPending = StytchUI.SMSPasscode.createUserAsPending,
            )

            when (result) {
                is StytchResult.Success -> {
                    val task = SmsRetriever.getClient(activity).startSmsUserConsent(null)
                    if (task.isSuccessful) Log.d("StytchLog", "SMS Retriever task started successfully")
                    else Log.w("StytchLog", "SMS Retriever task not started successfully (SMS autofill will not work)")
                    withContext(Dispatchers.Main) {
                        buttonText.value = R.string._continue
                        isButtonEnabled.value = true
                        navigator.goTo(
                            SMSPasscodeEnterPasscodeScreen(
                                methodId = result.value.phone_id,
                                phoneNumber = enteredPhoneNumber,
                            )
                        )
                    }
                }
                is StytchResult.Error -> {
                    if (result.errorCode in 400..499) {
                        withContext(Dispatchers.Main) {
                            buttonText.value = R.string._continue
                            isInErrorState.value = true
                            isPhoneNumberTextFieldInErrorState.value = true
                        }
                    }
                }
                StytchResult.NetworkError -> {

                }
            }
        }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        if (!phoneHintPickerShown) {
            phoneHintPickerShown = true
            val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()

            val intent = Credentials.getClient(activity).getHintPickerIntent(hintRequest)
            activity.startIntentSenderForResult(intent.intentSender, IntentCodes.PHONE_NUMBER_PICKER_INTENT_CODE.ordinal, null, 0, 0, 0)
        }
    }

    fun phoneNumberHintGiven(phoneNumber: String) {
        view.phoneNumberTextField.setText(phoneNumber.slice(2 until phoneNumber.length))
        onContinueButtonClicked()
    }
}

internal class SMSPasscodeHomeView(context: Context) : StytchScreenView<SMSPasscodeHomeScreen>(context) {
    val title: TextView
    val description: TextView
    val plusOneTextView: TextView
    val phoneNumberTextField: StytchEditText
    val smsConsentTextView: TextView
    val continueButton: Button
    val errorTextView: StytchErrorTextView

    init {
        inflate(context, R.layout.sms_enter_phone_number_layout, this)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        plusOneTextView = findViewById(R.id.plus_one_text_view)
        phoneNumberTextField = findViewById(R.id.phone_number_text_field)
        smsConsentTextView = findViewById(R.id.sms_consent_text_view)
        continueButton = findViewById(R.id.continue_button)
        errorTextView = findViewById(R.id.error_text_view)
        setBackgroundColor(resources.getColor(R.color.backgroundColor))
    }

    override fun subscribeToState() {
        screen.isInErrorState.subscribe {
            errorTextView.visibility = if (it) View.VISIBLE else View.GONE
        }
        screen.isPhoneNumberTextFieldInErrorState.subscribe {
            phoneNumberTextField.isInErrorState = it
        }
        screen.isButtonEnabled.subscribe {
            continueButton.isEnabled = it
        }
        screen.buttonText.subscribe {
            continueButton.text = resources.getString(it)
        }
        screen.errorMessage.subscribe {
            errorTextView.text = resources.getString(it)
        }
    }
}

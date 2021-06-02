package com.stytch.sdk

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SMSPasscodeHomeScreen : Screen<SMSPasscodeHomeView>() {
    var phoneHintPickerShown = false
    var inErrorState = false
    var errorMessage: String? = null
    var isPhoneNumberTextFieldInErrorState = false
    var buttonText: String? = null
    var isButtonEnabled = false
    var phoneNumberTextFieldText = ""

    override fun createView(context: Context): SMSPasscodeHomeView {
        return SMSPasscodeHomeView(context).apply {
            setBackgroundColor(StytchUI.uiCustomization.backgroundColor.getColor(context))

            phoneNumberTextField.addTextChangedListener {
                val asString = it.toString()
                if (asString != phoneNumberTextFieldText) {
                    phoneNumberTextFieldText = asString
                    isButtonEnabled = !view.phoneNumberTextField.text.isNullOrEmpty()
                    inErrorState = false
                    isPhoneNumberTextFieldInErrorState = false
                    view.updateIsButtonEnabled()
                    view.updateInErrorState()
                    view.updateIsPhoneNumberTextFieldInErrorState()
                }
            }

            if (buttonText == null) buttonText = resources.getString(R.string._continue)

            updateAllState(this@SMSPasscodeHomeScreen)

            continueButton.setOnClickListener { onContinueButtonClicked() }
        }
    }

    private fun onContinueButtonClicked() {
        val enteredPhoneNumber = view.phoneNumberTextField.text.toString()

        isButtonEnabled = false
        buttonText = activity.getString(R.string.sending_passcode)
        view.updateIsButtonEnabled()
        view.updateButtonText()

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
                        navigator.goTo(
                            SMSPasscodeEnterPasscodeScreen(
                                methodId = result.value.phone_id,
                                phoneNumber = enteredPhoneNumber,
                            )
                        )
                    }
                }
                is StytchResult.Error   -> {
                    if (result.errorCode in 400..499) {
                        withContext(Dispatchers.Main) {
                            buttonText = activity.getString(R.string._continue)
                            inErrorState = true
                            isPhoneNumberTextFieldInErrorState = true
                            view.updateButtonText()
                            view.updateInErrorState()
                            view.updateIsPhoneNumberTextFieldInErrorState()
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

internal class SMSPasscodeHomeView(context: Context) : BaseScreenView<SMSPasscodeHomeScreen>(context) {
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

    fun updateAllState(screen: SMSPasscodeHomeScreen) {
        updateInErrorState(screen)
        updateErrorMessage(screen)
        updateIsPhoneNumberTextFieldInErrorState(screen)
        updateButtonText(screen)
        updateIsButtonEnabled(screen)
    }

    fun updateInErrorState(screen: SMSPasscodeHomeScreen = this.screen) {
        errorTextView.visibility = if (screen.inErrorState) View.VISIBLE else View.GONE
    }

    fun updateErrorMessage(screen: SMSPasscodeHomeScreen = this.screen) {
        errorTextView.text = screen.errorMessage
    }

    fun updateIsPhoneNumberTextFieldInErrorState(screen: SMSPasscodeHomeScreen = this.screen) {
        phoneNumberTextField.isInErrorState = screen.isPhoneNumberTextFieldInErrorState
    }

    fun updateButtonText(screen: SMSPasscodeHomeScreen = this.screen) {
        continueButton.text = screen.buttonText
    }

    fun updateIsButtonEnabled(screen: SMSPasscodeHomeScreen = this.screen) {
        continueButton.isEnabled = screen.isButtonEnabled
    }
}

internal class SMSPasscodeEnterPasscodeScreen(
    private val methodId: String,
    private val phoneNumber: String,
) : Screen<SMSPasscodeEnterPasscodeView>() {
    override fun createView(context: Context): SMSPasscodeEnterPasscodeView {
        return SMSPasscodeEnterPasscodeView(context).apply {
            setBackgroundColor(StytchUI.uiCustomization.backgroundColor.getColor(context))
            description.text = resources.getString(R.string.passcode_sent_description, phoneNumber)
            resendCodeTextView.typeface = Typeface.create(resendCodeTextView.typeface, Typeface.BOLD)
            continueButton.isEnabled = false

            firstDigit.requestFocus()

            firstDigit.addTextChangedListener {
                updateContinueButtonState()
                if (!it.isNullOrEmpty()) {
                    firstDigit.clearFocus()
                    secondDigit.requestFocus()
                }
            }

            secondDigit.addTextChangedListener {
                updateContinueButtonState()
                secondDigit.clearFocus()
                (if (it.isNullOrEmpty()) firstDigit else thirdDigit).requestFocus()
            }

            thirdDigit.addTextChangedListener {
                updateContinueButtonState()
                thirdDigit.clearFocus()
                (if (it.isNullOrEmpty()) secondDigit else fourthDigit).requestFocus()
            }

            fourthDigit.addTextChangedListener {
                updateContinueButtonState()
                fourthDigit.clearFocus()
                (if (it.isNullOrEmpty()) thirdDigit else fifthDigit).requestFocus()
            }

            fifthDigit.addTextChangedListener {
                updateContinueButtonState()
                fifthDigit.clearFocus()
                (if (it.isNullOrEmpty()) fourthDigit else sixthDigit).requestFocus()
            }

            sixthDigit.addTextChangedListener {
                updateContinueButtonState()
                if (it.isNullOrEmpty()) {
                    sixthDigit.clearFocus()
                    fifthDigit.requestFocus()
                }
            }

            continueButton.setOnClickListener { onContinueButtonClicked() }
        }
    }

    private fun onContinueButtonClicked() {
        val enteredCode = view.firstDigit.text.toString() +
                view.secondDigit.text.toString() +
                view.thirdDigit.text.toString() +
                view.fourthDigit.text.toString() +
                view.fifthDigit.text.toString() +
                view.sixthDigit.text.toString()
        if (enteredCode.length < 6) TODO("Code not filled out")
        Log.d("EnteredCode", enteredCode)
        view.continueButton.isEnabled = false
        view.continueButton.text = view.resources.getString(R.string.verifying_code)
        GlobalScope.launch(Dispatchers.IO) {
            val result = StytchApi.OTP.authenticateOneTimePasscode(
                methodId = methodId,
                code = enteredCode,
            )

            when (result) {
                is StytchResult.Success -> {
                    Log.d("SMS_Authenticate", "Successful: ${result.value.user_id}")
                    activity.setResult(Activity.RESULT_OK, intentWithExtra(result.value))
                    activity.finish()
                }
                is StytchResult.Error   -> {
                    Log.d("SMS_Authenticate", "Error ${result.errorCode}: ${result.errorResponse}")
                    //StytchUIFlows.SMSOneTimePasscode.callback(StytchUIResult.failure(it))
                }
                StytchResult.NetworkError -> {

                }
            }
        }
    }

    fun smsReceived(messageText: String) {
        val digits = messageText.split(' ').last().toCharArray()
        view.apply {
            firstDigit.setText(digits, 0, 1)
            secondDigit.setText(digits, 1, 1)
            thirdDigit.setText(digits, 2, 1)
            fourthDigit.setText(digits, 3, 1)
            fifthDigit.setText(digits, 4, 1)
            sixthDigit.setText(digits, 5, 1)
        }
        onContinueButtonClicked()
    }
}

internal class SMSPasscodeEnterPasscodeView(context: Context) : BaseScreenView<SMSPasscodeEnterPasscodeScreen>(context) {
    val title: TextView
    val description: TextView
    val firstDigit: EditText
    val secondDigit: EditText
    val thirdDigit: EditText
    val fourthDigit: EditText
    val fifthDigit: EditText
    val sixthDigit: EditText
    val didntGetItTextView: TextView
    val resendCodeTextView: TextView
    val continueButton: Button

    init {
        inflate(context, R.layout.sms_enter_passcode_layout, this)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        firstDigit = findViewById(R.id.first_digit)
        secondDigit = findViewById(R.id.second_digit)
        thirdDigit = findViewById(R.id.third_digit)
        fourthDigit = findViewById(R.id.fourth_digit)
        fifthDigit = findViewById(R.id.fifth_digit)
        sixthDigit = findViewById(R.id.sixth_digit)
        didntGetItTextView = findViewById(R.id.didnt_get_it_text_view)
        resendCodeTextView = findViewById(R.id.resend_code_text_view)
        continueButton = findViewById(R.id.continue_button)
        setBackgroundColor(resources.getColor(R.color.backgroundColor))
    }

    fun areAllDigitsEntered(): Boolean {
        return firstDigit.text.isNotEmpty() &&
                secondDigit.text.isNotEmpty() &&
                thirdDigit.text.isNotEmpty() &&
                fourthDigit.text.isNotEmpty() &&
                fifthDigit.text.isNotEmpty() &&
                sixthDigit.text.isNotEmpty()
    }

    fun updateContinueButtonState() {
        continueButton.isEnabled = areAllDigitsEntered()
    }
}

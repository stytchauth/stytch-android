package com.stytch.sdk.screens

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.stytch.sdk.R
import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchResult
import com.stytch.sdk.StytchScreen
import com.stytch.sdk.StytchScreenView
import com.stytch.sdk.StytchUI
import com.stytch.sdk.intentWithExtra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class SMSPasscodeEnterPasscodeScreen(
    private val methodId: String,
    private val phoneNumber: String,
) : StytchScreen<SMSPasscodeEnterPasscodeView>() {
    var isInErrorState = MutableStateFlow(false)
    var errorMessage = MutableStateFlow<@StringRes Int>(R.string.unknown_error)
    var buttonText = MutableStateFlow<@StringRes Int>(R.string._continue)
    var isButtonEnabled = MutableStateFlow(false)

    override fun createView(context: Context): SMSPasscodeEnterPasscodeView {
        return SMSPasscodeEnterPasscodeView(context).apply {
            setBackgroundColor(StytchUI.uiCustomization.backgroundColor.getColor(context))
            description.text = resources.getString(R.string.passcode_sent_description, phoneNumber)
            resendCodeTextView.typeface = Typeface.create(resendCodeTextView.typeface, Typeface.BOLD)

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

        isButtonEnabled.value = false
        buttonText.value = R.string.verifying_code

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
                is StytchResult.Error -> {
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

    fun updateContinueButtonState() {
        isButtonEnabled.value = view.areAllDigitsEntered()
    }
}

internal class SMSPasscodeEnterPasscodeView(context: Context) : StytchScreenView<SMSPasscodeEnterPasscodeScreen>(context) {
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

    override fun subscribeToState() {
        screen.buttonText.subscribe {
            continueButton.text = resources.getString(it)
        }
        screen.isButtonEnabled.subscribe {
            continueButton.isEnabled = it
        }
    }

    fun areAllDigitsEntered(): Boolean {
        return firstDigit.text.isNotEmpty() &&
                secondDigit.text.isNotEmpty() &&
                thirdDigit.text.isNotEmpty() &&
                fourthDigit.text.isNotEmpty() &&
                fifthDigit.text.isNotEmpty() &&
                sixthDigit.text.isNotEmpty()
    }
}

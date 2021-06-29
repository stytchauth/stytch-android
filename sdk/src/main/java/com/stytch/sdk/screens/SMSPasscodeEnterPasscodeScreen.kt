package com.stytch.sdk.screens

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.stytch.sdk.R
import com.stytch.sdk.StytchErrorTextView
import com.stytch.sdk.StytchScreen
import com.stytch.sdk.StytchScreenView
import com.stytch.sdk.StytchSingleDigitEditText
import com.stytch.sdk.StytchUI
import kotlinx.coroutines.flow.MutableStateFlow

internal class SMSPasscodeEnterPasscodeScreen(
    private val methodId: String,
    private val phoneNumber: String,
) : StytchScreen<SMSPasscodeEnterPasscodeView>() {
    var isInErrorState = MutableStateFlow(false)
    var areDigitsInErrorState = MutableStateFlow(false)
    var errorMessage = MutableStateFlow<@StringRes Int>(R.string.unknown_error)
    var buttonText = MutableStateFlow<@StringRes Int>(R.string._continue)
    var isButtonEnabled = MutableStateFlow(false)

    override fun createView(context: Context): SMSPasscodeEnterPasscodeView {
        return SMSPasscodeEnterPasscodeView(context).apply {
            description.text = resources.getString(R.string.passcode_sent_description, phoneNumber)
            resendCodeTextView.typeface = Typeface.create(resendCodeTextView.typeface, Typeface.BOLD)

            firstDigit.requestFocus()

            firstDigit.addTextChangedListener {
                updateContinueButtonState()
                if (it.isNotNullOrEmpty()) {
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

        StytchUI.SMSPasscode.authenticator.apply {
            callback = this@SMSPasscodeEnterPasscodeScreen::onTokenAuthenticationComplete
            authenticateToken(enteredCode)
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
        areDigitsInErrorState.value = false
        isButtonEnabled.value = view.areAllDigitsEntered()
    }

    private fun onTokenAuthenticationComplete(success: Boolean) {
        if (success) {
            activity.finish()
        } else {
            isInErrorState.value = true
            areDigitsInErrorState.value = true
            errorMessage.value = R.string.invalid_passcode
            buttonText.value = R.string._continue
            isButtonEnabled.value = false
        }
    }
}

internal class SMSPasscodeEnterPasscodeView(context: Context) : StytchScreenView<SMSPasscodeEnterPasscodeScreen>(context) {
    val title: TextView
    val description: TextView
    val firstDigit: StytchSingleDigitEditText
    val secondDigit: StytchSingleDigitEditText
    val thirdDigit: StytchSingleDigitEditText
    val fourthDigit: StytchSingleDigitEditText
    val fifthDigit: StytchSingleDigitEditText
    val sixthDigit: StytchSingleDigitEditText
    val errorTextView: StytchErrorTextView
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
        errorTextView = findViewById(R.id.error_text_view)
        didntGetItTextView = findViewById(R.id.didnt_get_it_text_view)
        resendCodeTextView = findViewById(R.id.resend_code_text_view)
        continueButton = findViewById(R.id.continue_button)
    }

    override fun subscribeToState() {
        screen.errorMessage.subscribe {
            errorTextView.text = resources.getString(it)
        }
        screen.isInErrorState.subscribe {
            errorTextView.visibility = if (it) View.VISIBLE else View.GONE
        }
        screen.areDigitsInErrorState.subscribe {
            firstDigit.isInErrorState = it
            secondDigit.isInErrorState = it
            thirdDigit.isInErrorState = it
            fourthDigit.isInErrorState = it
            fifthDigit.isInErrorState = it
            sixthDigit.isInErrorState = it

        }
        screen.buttonText.subscribe {
            continueButton.text = resources.getString(it)
        }
        screen.isButtonEnabled.subscribe {
            continueButton.isEnabled = it
        }
    }

    fun areAllDigitsEntered(): Boolean {
        return firstDigit.text.isNotNullOrEmpty() &&
                secondDigit.text.isNotNullOrEmpty() &&
                thirdDigit.text.isNotNullOrEmpty() &&
                fourthDigit.text.isNotNullOrEmpty() &&
                fifthDigit.text.isNotNullOrEmpty() &&
                sixthDigit.text.isNotNullOrEmpty()
    }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun CharSequence?.isNotNullOrEmpty(): Boolean {
    return !isNullOrEmpty()
}

package com.stytch.sdk.screens

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.stytch.sdk.IntentCodes
import com.stytch.sdk.R
import com.stytch.sdk.StytchApi
import com.stytch.sdk.StytchEditText
import com.stytch.sdk.StytchErrorTextView
import com.stytch.sdk.StytchErrorType
import com.stytch.sdk.StytchResult
import com.stytch.sdk.StytchScreen
import com.stytch.sdk.StytchScreenView
import com.stytch.sdk.StytchUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class EmailMagicLinkHomeScreen : StytchScreen<EmailMagicLinkHomeView>() {
    var emailHintPickerShown = false
    var isEmailTextFieldInErrorState = MutableStateFlow(false)
    var errorMessage = MutableStateFlow<@StringRes Int>(R.string.unknown_error)
    var isInErrorState = MutableStateFlow(false)
    var buttonText = MutableStateFlow<@StringRes Int>(R.string._continue)
    var isButtonEnabled = MutableStateFlow(false)
    var currentTextFieldText = ""

    override fun createView(context: Context): EmailMagicLinkHomeView {
        return EmailMagicLinkHomeView(context).apply {
            emailTextField.apply {
                filters = arrayOf(
                    InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int ->
                        source.slice(start until end).filter { it != ' ' }
                    }
                )
                addTextChangedListener {
                    val asString = it.toString()
                    if (asString != currentTextFieldText) {
                        currentTextFieldText = asString
                        this@EmailMagicLinkHomeScreen.isInErrorState.value = false
                        isEmailTextFieldInErrorState.value = false
                        isButtonEnabled.value = asString.isValidEmailAddress()
                    }
                }
            }

            continueButton.setOnClickListener { onContinueButtonClicked() }
        }
    }

    private fun onContinueButtonClicked() {
        val enteredEmail = view.emailTextField.text.toString()

        isInErrorState.value = false
        isButtonEnabled.value = false
        buttonText.value = R.string.sending_email

        GlobalScope.launch(Dispatchers.IO) {
            val result = StytchApi.MagicLinks.Email.loginOrCreate(
                email = enteredEmail,
                loginMagicLinkUrl = StytchUI.EmailMagicLink.loginMagicLinkUrl,
                signupMagicLinkUrl = StytchUI.EmailMagicLink.signupMagicLinkUrl,
                createUserAsPending = StytchUI.EmailMagicLink.createUserAsPending,
            )

            withContext(Dispatchers.Main) {
                when (result) {
                    is StytchResult.Success -> {
                        buttonText.value = R.string._continue
                        isButtonEnabled.value = true
                        navigator.goTo(EmailMagicLinkConfirmationScreen(enteredEmail))
                    }
                    is StytchResult.NetworkError -> {
                        isInErrorState.value = true
                        errorMessage.value = R.string.network_error
                        buttonText.value = R.string._continue
                        isButtonEnabled.value = true
                    }
                    is StytchResult.Error -> {
                        when (result.errorType) {
                            StytchErrorType.EMAIL_NOT_FOUND, StytchErrorType.BILLING_NOT_VERIFIED_FOR_EMAIL -> {
                                isInErrorState.value = true
                                errorMessage.value = R.string.invalid_email_error
                                isEmailTextFieldInErrorState.value = true
                                buttonText.value = R.string._continue
                            }
                            else -> {
                                isInErrorState.value = true
                                errorMessage.value = R.string.unknown_error
                                buttonText.value = R.string._continue
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onShow(context: Context?) {
        super.onShow(context)
        if (!emailHintPickerShown) {
            emailHintPickerShown = true
            val hintRequest = HintRequest.Builder()
                .setEmailAddressIdentifierSupported(true)
                .build()

            val intent = Credentials.getClient(activity).getHintPickerIntent(hintRequest)
            activity.startIntentSenderForResult(intent.intentSender, IntentCodes.EMAIL_PICKER_INTENT_CODE.ordinal, null, 0, 0, 0)
        }
    }

    fun emailAddressHintGiven(emailAddress: String) {
        view.emailTextField.setText(emailAddress)
        onContinueButtonClicked()
    }

    override fun onAuthenticationError() {
        errorMessage.value = R.string.authentication_failed_please_try_again
        isInErrorState.value = true
    }
}

internal class EmailMagicLinkHomeView(context: Context) : StytchScreenView<EmailMagicLinkHomeScreen>(context) {
    val title: TextView
    val description: TextView
    val emailTextField: StytchEditText
    val errorTextView: StytchErrorTextView
    val continueButton: Button

    init {
        inflate(context, R.layout.magic_link_sign_up_or_log_in_layout, this)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        emailTextField = findViewById(R.id.email_text_field)
        errorTextView = findViewById(R.id.error_text_view)
        continueButton = findViewById(R.id.continue_button)
    }

    override fun subscribeToState() {
        screen.isEmailTextFieldInErrorState.subscribe {
            emailTextField.isInErrorState = it
        }
        screen.errorMessage.subscribe {
            errorTextView.text = resources.getString(it)
        }
        screen.isInErrorState.subscribe {
            errorTextView.visibility = if (it) View.VISIBLE else View.GONE
        }
        screen.buttonText.subscribe {
            continueButton.text = resources.getString(it)
        }
        screen.isButtonEnabled.subscribe {
            continueButton.isEnabled = it
        }
    }
}

internal fun String.isValidEmailAddress(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

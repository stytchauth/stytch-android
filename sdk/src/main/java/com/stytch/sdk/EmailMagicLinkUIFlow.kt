package com.stytch.sdk

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class EmailMagicLinkHomeScreen : Screen<EmailMagicLinkHomeView>() {
    var emailHintPickerShown = false
    var emailTextFieldInErrorState = false
    var errorMessage: String? = null
    var inErrorState = false
    var buttonText: String? = null
    var isButtonEnabled = false
    var currentTextFieldText = ""

    override fun createView(context: Context): EmailMagicLinkHomeView {
        return EmailMagicLinkHomeView(context).apply {
            setBackgroundColor(StytchUI.uiCustomization.backgroundColor.getColor(context))

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
                        inErrorState = false
                        emailTextFieldInErrorState = false
                        isButtonEnabled = it?.toString()?.isValidEmailAddress() == true
                        view.updateInErrorState()
                        view.updateEmailTextFieldInErrorState()
                        view.updateIsButtonEnabled()
                    }
                }
            }

            continueButton.setOnClickListener { onContinueButtonClicked() }

            if (buttonText == null) buttonText = resources.getString(R.string._continue)

            updateAllState(this@EmailMagicLinkHomeScreen)
        }
    }

    private fun onContinueButtonClicked() {
        val enteredEmail = view.emailTextField.text.toString()

        inErrorState = false
        isButtonEnabled = false
        buttonText = view.resources.getString(R.string.sending_email)
        view.updateInErrorState()
        view.updateIsButtonEnabled()
        view.updateButtonText()

        GlobalScope.launch(Dispatchers.IO) {
            val result = StytchApi.MagicLinks.loginOrCreateUserByEmail(
                email = enteredEmail,
                loginMagicLinkUrl = StytchUI.EmailMagicLink.loginMagicLinkUrl,
                signupMagicLinkUrl = StytchUI.EmailMagicLink.signupMagicLinkUrl,
                createUserAsPending = StytchUI.EmailMagicLink.createUserAsPending,
            )

            withContext(Dispatchers.Main) {
                when (result) {
                    is StytchResult.Success -> {
                        buttonText = activity.getString(R.string._continue)
                        isButtonEnabled = true
                        navigator.goTo(EmailMagicLinkConfirmationScreen(enteredEmail))
                    }
                    is StytchResult.NetworkError -> {
                        inErrorState = true
                        errorMessage = activity.getString(R.string.network_error)
                        buttonText = activity.getString(R.string._continue)
                        isButtonEnabled = true
                        view.updateInErrorState()
                        view.updateErrorMessage()
                        view.updateButtonText()
                        view.updateIsButtonEnabled()
                    }
                    is StytchResult.Error   -> {
                        when (result.errorResponse?.error_type) {
                            null -> {
                                inErrorState = true
                                errorMessage = activity.getString(R.string.unknown_error)
                                buttonText = activity.getString(R.string._continue)
                                view.updateInErrorState()
                                view.updateErrorMessage()
                                view.updateButtonText()
                            }
                            StytchErrorTypes.EMAIL_NOT_FOUND, StytchErrorTypes.BILLING_NOT_VERIFIED_FOR_EMAIL -> {
                                inErrorState = true
                                errorMessage = activity.getString(R.string.invalid_email_error)
                                emailTextFieldInErrorState = true
                                buttonText = activity.getString(R.string._continue)
                                view.updateInErrorState()
                                view.updateErrorMessage()
                                view.updateEmailTextFieldInErrorState()
                                view.updateButtonText()
                            }
                            else -> {
                                inErrorState = true
                                errorMessage = activity.getString(R.string.unknown_error)
                                buttonText = activity.getString(R.string._continue)
                                view.updateInErrorState()
                                view.updateErrorMessage()
                                view.updateButtonText()
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
}

internal class EmailMagicLinkHomeView(context: Context) : BaseScreenView<EmailMagicLinkHomeScreen>(context) {
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

    fun updateAllState(screen: EmailMagicLinkHomeScreen) {
        updateButtonText(screen)
        updateErrorMessage(screen)
        updateInErrorState(screen)
        updateEmailTextFieldInErrorState(screen)
        updateIsButtonEnabled(screen)
    }

    fun updateButtonText(screen: EmailMagicLinkHomeScreen = this.screen) {
        continueButton.text = screen.buttonText
    }

    fun updateErrorMessage(screen: EmailMagicLinkHomeScreen = this.screen) {
        errorTextView.text = screen.errorMessage
    }

    fun updateInErrorState(screen: EmailMagicLinkHomeScreen = this.screen) {
        errorTextView.visibility = if (screen.inErrorState) View.VISIBLE else View.GONE
    }

    fun updateEmailTextFieldInErrorState(screen: EmailMagicLinkHomeScreen = this.screen) {
        emailTextField.isInErrorState = screen.emailTextFieldInErrorState
    }

    fun updateIsButtonEnabled(screen: EmailMagicLinkHomeScreen = this.screen) {
        continueButton.isEnabled = screen.isButtonEnabled
    }
}

internal class EmailMagicLinkConfirmationScreen(
    private val emailAddress: String,
) : Screen<EmailMagicLinkConfirmationView>() {
    override fun createView(context: Context): EmailMagicLinkConfirmationView {
        return EmailMagicLinkConfirmationView(context).apply {
            setBackgroundColor(StytchUI.uiCustomization.backgroundColor.getColor(context))
            description.text = resources.getString(R.string.magic_link_sent_description, emailAddress)

            tryAgainButton.setOnClickListener {
                navigator.goBack()
            }
        }
    }
}

internal class EmailMagicLinkConfirmationView(context: Context) : BaseScreenView<EmailMagicLinkConfirmationScreen>(context) {
    val title: TextView
    val description: TextView
    val tryAgainButton: Button

    init {
        inflate(context, R.layout.magic_link_check_your_email_layout, this)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        tryAgainButton = findViewById(R.id.try_again_button)
    }
}

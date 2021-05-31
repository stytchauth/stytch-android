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

internal class EmailMagicLinkHomeScreen(private val configuration: StytchUI.EmailMagicLink.Configuration) : Screen<EmailMagicLinkHomeView>() {
    private var emailHintPickerShown = false

    override fun createView(context: Context): EmailMagicLinkHomeView {
        return EmailMagicLinkHomeView(context).apply {
            setBackgroundColor(StytchUI.uiCustomization.backgroundColor.getColor(context))

            emailTextField.filters = arrayOf(
                InputFilter { source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int ->
                    source.slice(start until end).filter { it != ' ' }
                }
            )
            emailTextField.addTextChangedListener {
                view.errorTextView.visibility = View.GONE
                view.emailTextField.isInErrorState = false
                continueButton.isEnabled = it?.toString()?.isValidEmailAddress() == true
            }

            continueButton.isEnabled = false
            //emailTextField.requestFocus()

            continueButton.setOnClickListener { onContinueButtonClicked() }
        }
    }

    private fun onContinueButtonClicked() {
        view.errorTextView.visibility = View.GONE
        val enteredEmail = view.emailTextField.text.toString()
        view.continueButton.isEnabled = false
        view.continueButton.text = view.resources.getString(R.string.sending_email)
        GlobalScope.launch(Dispatchers.IO) {
            val result = StytchApi.MagicLinks.loginOrCreateUserByEmail(
                email = enteredEmail,
                loginMagicLinkUrl = configuration.loginMagicLinkUrl,
                signupMagicLinkUrl = configuration.signupMagicLinkUrl,
                createUserAsPending = configuration.createUserAsPending,
            )

            withContext(Dispatchers.Main) {
                when (result) {
                    is StytchResult.Success -> {
                        navigator.goTo(EmailMagicLinkConfirmationScreen(enteredEmail))
                    }
                    is StytchResult.NetworkError -> {
                        view.errorTextView.show(R.string.network_error)
                        view.continueButton.setText(R.string._continue)
                    }
                    is StytchResult.Error   -> {
                        when (result.errorResponse?.error_type) {
                            null -> {
                                view.errorTextView.show(R.string.unknown_error)
                                view.continueButton.setText(R.string._continue)
                            }
                            StytchErrorTypes.EMAIL_NOT_FOUND, StytchErrorTypes.BILLING_NOT_VERIFIED_FOR_EMAIL -> {
                                view.errorTextView.show(R.string.invalid_email_error)
                                view.emailTextField.isInErrorState = true
                                view.continueButton.setText(R.string._continue)
                            }
                            else -> {
                                view.errorTextView.show(R.string.unknown_error)
                                view.continueButton.setText(R.string._continue)
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

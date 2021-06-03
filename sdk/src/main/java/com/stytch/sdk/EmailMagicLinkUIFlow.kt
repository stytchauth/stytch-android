package com.stytch.sdk

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class EmailMagicLinkHomeScreen : Screen<EmailMagicLinkHomeView>() {
    var emailHintPickerShown = false
    var isEmailTextFieldInErrorState = MutableStateFlow(false)
    var errorMessage = MutableStateFlow<@StringRes Int>(R.string.invalid_email_error)
    var isInErrorState = MutableStateFlow(false)
    var buttonText = MutableStateFlow<@StringRes Int>(R.string._continue)
    var isButtonEnabled = MutableStateFlow(false)
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
            val result = StytchApi.MagicLinks.loginOrCreateUserByEmail(
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
                        when (result.errorResponse?.error_type) {
                            null -> {
                                isInErrorState.value = true
                                errorMessage.value = R.string.unknown_error
                                buttonText.value = R.string._continue
                            }
                            StytchErrorTypes.EMAIL_NOT_FOUND, StytchErrorTypes.BILLING_NOT_VERIFIED_FOR_EMAIL -> {
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

        view.coroutineScope = StytchScreenViewCoroutineScope()
        view.subscribeToState()
    }

    override fun onHide(context: Context?) {
        super.onHide(context)
        view.coroutineScope.cancel()
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
    lateinit var coroutineScope: CoroutineScope

    init {
        inflate(context, R.layout.magic_link_sign_up_or_log_in_layout, this)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        emailTextField = findViewById(R.id.email_text_field)
        errorTextView = findViewById(R.id.error_text_view)
        continueButton = findViewById(R.id.continue_button)
    }

    fun subscribeToState() {
        with(coroutineScope) {
            listen(screen.isEmailTextFieldInErrorState) {
                emailTextField.isInErrorState = it
            }
            listen(screen.errorMessage) {
                errorTextView.text = resources.getString(it)
            }
            listen(screen.isInErrorState) {
                errorTextView.visibility = if (it) View.VISIBLE else View.GONE
            }
            listen(screen.buttonText) {
                continueButton.text = resources.getString(it)
            }
            listen(screen.isButtonEnabled) {
                continueButton.isEnabled = it
            }
        }
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

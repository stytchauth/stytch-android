package com.stytch.sdk.screens

import android.content.Context
import android.widget.Button
import android.widget.TextView
import com.stytch.sdk.R
import com.stytch.sdk.StytchScreen
import com.stytch.sdk.StytchScreenView
import com.stytch.sdk.StytchUI

internal class EmailMagicLinkConfirmationScreen(
    private val emailAddress: String,
) : StytchScreen<EmailMagicLinkConfirmationView>() {
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

internal class EmailMagicLinkConfirmationView(context: Context) : StytchScreenView<EmailMagicLinkConfirmationScreen>(context) {
    val title: TextView
    val description: TextView
    val tryAgainButton: Button

    init {
        inflate(context, R.layout.magic_link_check_your_email_layout, this)
        title = findViewById(R.id.title)
        description = findViewById(R.id.description)
        tryAgainButton = findViewById(R.id.try_again_button)
    }

    override fun subscribeToState() {
    }
}

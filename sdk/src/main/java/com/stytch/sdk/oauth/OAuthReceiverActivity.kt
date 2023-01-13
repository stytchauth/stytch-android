package com.stytch.sdk.oauth

import android.app.Activity
import android.os.Bundle

internal class OAuthReceiverActivity : Activity() {
    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        startActivity(OAuthManagerActivity.createResponseHandlingIntent(this, intent.data))
        finish()
    }
}

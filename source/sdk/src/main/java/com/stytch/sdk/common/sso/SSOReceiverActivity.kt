package com.stytch.sdk.common.sso

import android.app.Activity
import android.os.Bundle

internal class SSOReceiverActivity : Activity() {
    override fun onCreate(savedInstanceBundle: Bundle?) {
        super.onCreate(savedInstanceBundle)
        startActivity(SSOManagerActivity.createResponseHandlingIntent(this, intent.data))
        finish()
    }
}

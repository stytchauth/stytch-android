package com.stytch.sdk.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.stytch.sdk.common.StytchExceptions
import com.stytch.sdk.common.StytchResult

internal class StytchAuthenticationContract : ActivityResultContract<StytchProductConfig, StytchResult<*>>() {
    override fun createIntent(context: Context, input: StytchProductConfig): Intent =
        AuthenticationActivity.createIntent(context, input)

    override fun parseResult(resultCode: Int, intent: Intent?): StytchResult<*> {
        if (resultCode != Activity.RESULT_OK) {
            return StytchResult.Error(StytchExceptions.Input("Activity failed with resultCode = $resultCode"))
        }
        return intent?.extras?.getParcelable(AuthenticationActivity.STYTCH_RESULT_KEY)
            ?: StytchResult.Error(StytchExceptions.Input("Failed to retrieve data from intent"))
    }
}

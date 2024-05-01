package com.stytch.sdk.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.errors.StytchUIActivityFailed
import com.stytch.sdk.common.errors.StytchUINoDataFromIntent
import com.stytch.sdk.ui.data.StytchUIConfig

internal class StytchAuthenticationContract : ActivityResultContract<StytchUIConfig, StytchResult<*>>() {
    override fun createIntent(
        context: Context,
        input: StytchUIConfig,
    ): Intent = AuthenticationActivity.createIntent(context, input)

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): StytchResult<*> {
        if (resultCode != Activity.RESULT_OK) {
            return StytchResult.Error(StytchUIActivityFailed(resultCode))
        }
        return intent?.extras?.getParcelable(AuthenticationActivity.STYTCH_RESULT_KEY)
            ?: StytchResult.Error(StytchUINoDataFromIntent)
    }
}

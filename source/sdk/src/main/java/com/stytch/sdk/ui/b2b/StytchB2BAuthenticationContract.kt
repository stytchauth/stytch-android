package com.stytch.sdk.ui.b2b

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.stytch.sdk.common.errors.StytchUIActivityFailed
import com.stytch.sdk.common.errors.StytchUINoDataFromIntent
import com.stytch.sdk.ui.b2b.data.AuthenticationResult
import com.stytch.sdk.ui.b2b.data.StytchB2BUIConfig

internal class StytchB2BAuthenticationContract : ActivityResultContract<StytchB2BUIConfig, AuthenticationResult>() {
    override fun createIntent(
        context: Context,
        input: StytchB2BUIConfig,
    ): Intent = B2BAuthenticationActivity.createIntent(context, input)

    override fun parseResult(
        resultCode: Int,
        intent: Intent?,
    ): AuthenticationResult {
        if (resultCode != Activity.RESULT_OK) {
            return AuthenticationResult.Error(StytchUIActivityFailed(resultCode))
        }
        return intent?.extras?.getParcelable(B2BAuthenticationActivity.STYTCH_RESULT_KEY)
            ?: AuthenticationResult.Error(StytchUINoDataFromIntent)
    }
}

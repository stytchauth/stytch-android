package com.stytch.sdk.common.dfp

import android.app.Application
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal interface CaptchaProvider {
    suspend fun executeRecaptcha(): String
}

internal class CaptchaProviderImpl(
    application: Application,
    scope: CoroutineScope,
    siteKey: String,
) : CaptchaProvider {

    private lateinit var recaptchaClient: RecaptchaClient

    init {
        scope.launch(Dispatchers.Main) {
            recaptchaClient = Recaptcha.getClient(application, siteKey).getOrThrow()
        }
    }

    override suspend fun executeRecaptcha(): String =
        recaptchaClient.execute(RecaptchaAction.LOGIN).getOrThrow()
}

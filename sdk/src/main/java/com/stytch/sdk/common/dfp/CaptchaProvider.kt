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
            Recaptcha.getClient(application, siteKey).onSuccess {
                recaptchaClient = it
            }
        }
    }

    override suspend fun executeRecaptcha(): String =
        if (::recaptchaClient.isInitialized) {
            recaptchaClient.execute(RecaptchaAction.LOGIN).getOrElse { "" }
        } else {
            ""
        }
}

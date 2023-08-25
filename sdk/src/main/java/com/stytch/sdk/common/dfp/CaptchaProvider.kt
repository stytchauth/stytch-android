package com.stytch.sdk.common.dfp

import android.app.Application
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient

internal interface CaptchaProvider {
    suspend fun initializeRecaptchaClient(siteKey: String)
    suspend fun executeRecaptcha(): String
}

internal class CaptchaProviderImpl(
    private val application: Application
): CaptchaProvider {

    private lateinit var recaptchaClient: RecaptchaClient

    override suspend fun initializeRecaptchaClient(siteKey: String) {
        recaptchaClient = Recaptcha.getClient(application, siteKey).getOrElse { exception ->
            // TODO: handle errors
            return
        }
    }

    override suspend fun executeRecaptcha(): String {
        return recaptchaClient.execute(RecaptchaAction.LOGIN).getOrElse { exception ->
            // TODO: handle errors
            return ""
        }
    }
}

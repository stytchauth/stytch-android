package com.stytch.sdk.common.dfp

import android.app.Application
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient

internal interface CaptchaProvider {
    suspend fun initializeRecaptchaClient()
    suspend fun executeRecaptcha(): String
}

internal class CaptchaProviderImpl(
    private val application: Application,
    private val siteKey: String
): CaptchaProvider {

    private lateinit var recaptchaClient: RecaptchaClient

    override suspend fun initializeRecaptchaClient() {
        recaptchaClient = Recaptcha.getClient(application, siteKey).getOrThrow()
    }

    override suspend fun executeRecaptcha(): String {
        return recaptchaClient.execute(RecaptchaAction.LOGIN).getOrThrow()
    }
}

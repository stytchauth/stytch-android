package com.stytch.sdk.common.smsRetriever

internal interface StytchSMSRetriever {
    fun start(sessionDurationMinutes: Int)

    fun finish()
}

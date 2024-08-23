package com.stytch.sdk.common.smsRetriever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.stytch.sdk.common.StytchLog

internal class StytchSMSBroadcastReceiver(
    private val onMessageReceived: (String?) -> Unit,
) : BroadcastReceiver() {
    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
        intent.extras?.let { extras ->
            when (val status = extras.getParcelable<Status>(SmsRetriever.EXTRA_STATUS)) {
                Status(CommonStatusCodes.SUCCESS) -> {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE) ?: return@let
                    val codeParsedFromMessage = parseCodeFromMessage(message)
                    onMessageReceived(codeParsedFromMessage)
                }
                Status(CommonStatusCodes.TIMEOUT) -> StytchLog.d("StytchSMSBroadcastReceiver timed out")
                else -> StytchLog.e("StytchSMSBroadcastReceiver unexpected status: $status")
            }
        }
    }

    private fun parseCodeFromMessage(message: String): String? = "\\d{6}".toRegex().find(message)?.value
}

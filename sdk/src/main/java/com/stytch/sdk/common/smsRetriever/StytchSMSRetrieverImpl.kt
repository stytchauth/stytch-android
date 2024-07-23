package com.stytch.sdk.common.smsRetriever

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.stytch.sdk.common.StytchLog

@SuppressLint("UnspecifiedRegisterReceiverFlag")
internal class StytchSMSRetrieverImpl(
    private val context: Context,
    callback: (String?) -> Unit,
) : StytchSMSRetriever {
    init {
        val broadcastReceiver = StytchSMSBroadcastReceiver(callback)
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        val permission = SmsRetriever.SEND_PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                broadcastReceiver,
                intentFilter,
                permission,
                null,
                Context.RECEIVER_EXPORTED,
            )
        } else {
            context.registerReceiver(broadcastReceiver, intentFilter, permission, null)
        }
    }

    override fun start() {
        val client = SmsRetriever.getClient(context)
        client
            .startSmsRetriever()
            .addOnSuccessListener {
                StytchLog.d("Successfully started SMS Retriever Client")
            }.addOnFailureListener {
                StytchLog.e(it.message ?: "Failed to start SMS Retriever Client")
            }.addOnCanceledListener {
                StytchLog.d("SMS Retriever Client canceled")
            }.addOnCompleteListener {
                StytchLog.d("SMS Retriever Client completed")
            }
    }
}

package com.stytch.sdk.common.dfp

import android.content.Context

internal interface DFPProvider {
    fun getTelemetryId(): String
}

internal class DFPProviderImpl(
    private val context: Context,
    private val publicToken: String,
) : DFPProvider {
    override fun getTelemetryId(): String {
        context.startActivity(DFPActivity.createIntent(context, publicToken))
        return ""
    }
}

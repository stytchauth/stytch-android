package com.stytch.sdk.common.dfp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

internal class DFPActivity : Activity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(true)
        val publicToken = intent.getStringExtra(PUBLIC_TOKEN_KEY)
        val dfpWebView = WebView(this)
        dfpWebView.setBackgroundColor(Color.TRANSPARENT)
        dfpWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.evaluateJavascript("fetchTelemetryId('$publicToken');", null)
            }
        }
        dfpWebView.settings.javaScriptEnabled = true
        dfpWebView.settings.databaseEnabled = true
        dfpWebView.settings.allowFileAccess = true
        dfpWebView.settings.domStorageEnabled = true
        dfpWebView.addJavascriptInterface(stytchDfpInterface, "StytchDFP")
        dfpWebView.loadUrl("file:///android_asset/dfp.html")
        setContentView(dfpWebView)
    }

    private val stytchDfpInterface = object {
        @JavascriptInterface
        fun reportTelemetryId(telemetryId: String) {
            val data = Intent().apply {
                putExtra(TELEMETRY_ID_KEY, telemetryId)
            }
            setResult(RESULT_OK, data)
            finish()
        }
    }

    internal companion object {
        internal const val TELEMETRY_ID_KEY = "dfp_telemetry_id"
        internal const val PUBLIC_TOKEN_KEY = "public_token"
        internal fun createIntent(context: Context, publicToken: String) =
            Intent(context, DFPActivity::class.java).apply {
                putExtra(PUBLIC_TOKEN_KEY, publicToken)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
    }
}

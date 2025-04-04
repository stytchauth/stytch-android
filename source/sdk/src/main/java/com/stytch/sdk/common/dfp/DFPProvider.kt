package com.stytch.sdk.common.dfp

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal interface DFPProvider {
    suspend fun getTelemetryId(): String
}

internal class DFPProviderImpl(
    private val publicToken: String,
    private val dfppaDomain: String,
    private val activityProvider: ActivityProvider,
) : DFPProvider {
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    // for some reason the linter isn't detecting the @JavascriptInterface annotation, so we need to suppress it
    private fun createWebView(context: Context): WebView {
        val dfpWebView = WebView(context)
        dfpWebView.webViewClient =
            object : WebViewClient() {
                override fun onPageFinished(
                    view: WebView,
                    url: String,
                ) {
                    view.evaluateJavascript("fetchTelemetryId('$publicToken', 'https://$dfppaDomain/submit');", null)
                }
            }
        dfpWebView.settings.javaScriptEnabled = true
        dfpWebView.settings.databaseEnabled = true
        dfpWebView.settings.domStorageEnabled = true
        dfpWebView.addJavascriptInterface(stytchDfpInterface, "StytchDFP")
        dfpWebView.loadUrl("file:///android_asset/dfp.html")
        return dfpWebView
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val stytchDfpInterface =
        object {
            @JavascriptInterface
            fun reportTelemetryId(telemetryId: String) {
                activityProvider.currentActivity?.let { currentActivity ->
                    webview?.let {
                        currentActivity.runOnUiThread {
                            (it.parent as? ViewGroup)?.removeView(it)
                        }
                    }
                }
                webview = null
                if (continuation.isActive) {
                    continuation.resume(telemetryId, null)
                }
            }
        }

    private lateinit var continuation: CancellableContinuation<String>

    private var webview: WebView? = null

    override suspend fun getTelemetryId(): String =
        suspendCancellableCoroutine { cont ->
            continuation = cont
            activityProvider.currentActivity?.let {
                it.runOnUiThread {
                    webview = createWebView(it)
                    it.addContentView(webview, ViewGroup.LayoutParams(0, 0))
                }
            } ?: run {
                // Couldn't inject webview, return empty string
                continuation.resume("")
            }
        }
}

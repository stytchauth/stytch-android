package com.stytch.sdk.common.dfp

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import com.stytch.dfp.DFP as StytchDFP

internal interface DFPProvider {
    suspend fun getTelemetryId(): String
}

internal class DFPProviderImpl(
    scope: CoroutineScope,
    context: Context,
    private val publicToken: String,
    private val dfppaDomain: String,
    private val dfpType: DFPType,
    private val activityProvider: WebviewActivityProvider? = null,
) : DFPProvider {
    private var dfp: StytchDFP? = null
    private var continuation: CancellableContinuation<String>? = null
    private var webview: WebView? = null

    init {
        if (dfpType == DFPType.Native) {
            scope.launch(Dispatchers.IO) {
                dfp =
                    try {
                        StytchDFP(context = context, publicToken = publicToken, submissionUrl = dfppaDomain)
                    } catch (_: UnsatisfiedLinkError) {
                        null
                    } catch (_: NoClassDefFoundError) {
                        null
                    }
            }
        }
    }

    override suspend fun getTelemetryId(): String =
        suspendCancellableCoroutine { cont ->
            if (dfpType == DFPType.Native) {
                dfp?.getTelemetryId { telemetryId ->
                    cont.resume(telemetryId)
                } ?: run {
                    cont.resume(STATIC_UNABLE_TO_LOAD_BINARY)
                }
            } else {
                continuation = cont
                activityProvider?.currentActivity?.let {
                    it.runOnUiThread {
                        webview = createWebView(it)
                        it.addContentView(webview, ViewGroup.LayoutParams(0, 0))
                    }
                } ?: run {
                    cont.resume(STATIC_UNABLE_TO_ACCESS_WEBVIEW)
                }
            }
        }

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
                activityProvider?.currentActivity?.let { currentActivity ->
                    webview?.let {
                        currentActivity.runOnUiThread {
                            (it.parent as? ViewGroup)?.removeView(it)
                        }
                    }
                }
                webview = null
                if (continuation?.isActive == true) {
                    continuation?.resume(telemetryId, null)
                }
                continuation = null
            }
        }

    companion object {
        const val STATIC_UNABLE_TO_LOAD_BINARY = "9b595d97-f845-4de6-b0ef-014905bc92dc"
        const val STATIC_UNABLE_TO_ACCESS_WEBVIEW = "cf147d0c-2948-4187-8f82-1c41d087284a"
    }
}

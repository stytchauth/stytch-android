package com.stytch.sdk.oauth

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent

internal class OAuthManagerActivity : Activity() {
    private var authorizationStarted = false
    private lateinit var desiredUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            setDesiredUriFromBundle(intent.extras)
        } else {
            setDesiredUriFromBundle(savedInstanceState)
        }
    }

    override fun onResume() {
        super.onResume()
        // on first run, launch the intent to start the OAuth flow in the browser
        if (!authorizationStarted) {
            try {
                val authorizationIntent = generateIntentForUri(desiredUri)
                startActivity(authorizationIntent)
                authorizationStarted = true
            } catch (e: ActivityNotFoundException) {
                noBrowserFound()
                finish()
            }
            return
        }
        // subsequent runs, we either got the response back from OAuthReceiverActivity or it was cancelled
        intent.data?.let { authorizationComplete(it) } ?: authorizationCanceled()
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun setDesiredUriFromBundle(bundle: Bundle?) {
        if (bundle == null) return finish()
        bundle.getString(URI_KEY)?.let {
            desiredUri = Uri.parse(it)
        }
    }

    private fun generateIntentForUri(uri: Uri): Intent {
        // if using custom tabs browser
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.intent.data = uri
        return customTabsIntent.intent
        // else
    }

    private fun authorizationComplete(uri: Uri) {
        // TODO: parse URI and make sure it is not a failure response
        val response = Intent().apply {
            data = uri
        }
        setResult(RESULT_OK, response)
    }

    private fun authorizationCanceled() {
        // TODO: add info as to why it was canceled
        val response = Intent()
        setResult(RESULT_CANCELED, response)
    }

    private fun noBrowserFound() {
        // TODO: add info as to why it was canceled
        val response = Intent()
        setResult(RESULT_CANCELED, response)
    }

    companion object {
        internal fun createResponseHandlingIntent(context: Context, responseUri: Uri?): Intent {
            val intent = createBaseIntent(context)
            intent.data = responseUri
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            return intent
        }

        internal fun createBaseIntent(context: Context): Intent {
            return Intent(context, OAuthManagerActivity::class.java)
        }
        internal const val URI_KEY = "uri"
    }
}

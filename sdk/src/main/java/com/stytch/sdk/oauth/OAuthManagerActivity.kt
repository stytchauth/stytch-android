package com.stytch.sdk.oauth

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent

public enum class OAuthError(public val message: String) {
    NO_BROWSER_FOUND("No supported browser was found on this device"),
    NO_URI_FOUND("No OAuth URI could be found in the bundle"),
    USER_CANCELED("The user canceled the OAuth flow"),
    OAUTH_ERROR_STATE("The returned OAuth URI is invalid"),
}

public class OAuthManagerActivity : Activity() {
    private var authorizationStarted = false
    private lateinit var desiredUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            hydrateState(intent.extras)
        } else {
            hydrateState(savedInstanceState)
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
            } catch (_: UninitializedPropertyAccessException) {
                noUriFound()
                finish()
            } catch (_: ActivityNotFoundException) {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_AUTHORIZATION_STARTED, authorizationStarted)
    }

    private fun hydrateState(state: Bundle?) {
        if (state == null) return finish()
        authorizationStarted = state.getBoolean(KEY_AUTHORIZATION_STARTED, false)
        state.getString(URI_KEY)?.let {
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
        val response = Intent().apply { data = uri }
        setResult(RESULT_OK, response)
    }

    private fun authorizationCanceled() {
        val response = Intent()
        intent.putExtra(OAUTH_ERROR, OAuthError.USER_CANCELED.message)
        setResult(RESULT_CANCELED, response)
    }

    private fun noBrowserFound() {
        val response = Intent()
        intent.putExtra(OAUTH_ERROR, OAuthError.NO_BROWSER_FOUND.message)
        setResult(RESULT_CANCELED, response)
    }

    private fun noUriFound() {
        val response = Intent()
        intent.putExtra(OAUTH_ERROR, OAuthError.NO_URI_FOUND.message)
        setResult(RESULT_CANCELED, response)
    }

    public companion object {
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
        public const val OAUTH_ERROR: String = "StytchOAuthError"
        public const val OAUTH_RESPONSE: String = "StytchOAuthResponse"
        private const val KEY_AUTHORIZATION_STARTED = "authStarted"
    }
}

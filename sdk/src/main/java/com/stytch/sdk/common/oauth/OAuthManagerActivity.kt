package com.stytch.sdk.common.oauth

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import com.stytch.sdk.common.oauth.OAuthError.Companion.OAUTH_EXCEPTION

/**
 * Represents one of three potential errors encountered during the Third party OAuth authentication flow.
 * @property message A friendly string indicating what wrong during the OAuth authentication flow.
 */
public sealed class OAuthError(override val message: String) : Exception(message) {
    /**
     * Indicates that no suitable browser was found on this device, and OAuth authentication cannot proceed.
     */
    public object NoBrowserFound : OAuthError("No supported browser was found on this device")

    /**
     * Indicates that no URI was found in the activity state, and OAuth authentication cannot proceed.
     */
    public object NoURIFound : OAuthError("No OAuth URI could be found in the bundle")

    /**
     * Indicates that the user canceled the OAuth flow. This is safe to ignore.
     */
    public object UserCanceled : OAuthError("The user canceled the OAuth flow")

    public companion object {
        /**
         * A string identifying the class of this Exception for serializing/deserializing the error within an Intent
         */
        public const val OAUTH_EXCEPTION: String = "com.stytch.sdk.oauth.OAuthError"
    }
}

/**
 * State management activity for OAuth flow. This is based on the functionality of AppAuth-Android
 *
 * The following diagram illustrates the operation of the activity:
 *
 *                          Back Stack Towards Top
 *                +------------------------------------------>
 *
 * +------------+            +---------------+      +----------------+      +--------------+
 * |            |     (1)    |               | (2)  |                | (S1) |              |
 * | Initiating +----------->| OAuthManager  +----->| Authorization  +----->| OAuthReceiver|
 * |  Activity  |            |   Activity    |      |   Activity     |      |   Activity   |
 * |            |<-----------+               |<-----+ (e.g. browser) |      |              |
 * |            | (S3, C2)   |               | (C1) |                |      |              |
 * +------------+            +-------+-------+      +----------------+      +-------+------+
 *                                   ^                                              |
 *                                   |                   (S2)                       |
 *                                   +----------------------------------------------+
 *
 * - Step 1: ThirdPartyOAuth intiates an intent which launches this (no-ui) activity
 * - Step 2: This activity determines the best browser to launch the authorization flow in, and launches it. Depending
 *   on user action, we then enter either a cancellation (C)  or success (S) flow
 *
 * Cancellation (C) flow:
 * If the user cancels the authorization, we are returned to this Activity at the top of the backstack (C1). Since no
 * return URI is provided, we know the user cancelled, and return a RESULT_CANCELED result for the original intent (C2)
 * and finish the activity. The calling activity will listen for this result and either provide messaging for the user
 * if the error returned is one of NO_BROWSER_FOUND or NO_URI_FOUND, or (most likely) do nothing if it is USER_CANCELED.
 *
 * Success (S) flow:
 * When the user completes authorization, the OAuthReceiverActivity is launched (S1), as specified in the manifest. That
 * activity will launch this activity (S2) via an intent with CLEAR_TOP set, so that the authorization activity and
 * receiver activity are destroyed leaving this activity at the top of the backstack. This activity will then return a
 * RESULT_OK status for the original intent and pass along the returned URI, then finish itself (S3). The calling
 * activity will listen for this result and use the returned URI to make the authorization call to the Stytch API.
 */
internal class OAuthManagerActivity : Activity() {
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
                val browser = BrowserSelector.getBestBrowser(this) ?: throw ActivityNotFoundException()
                val authorizationIntent = generateIntentForUri(browser, desiredUri)
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

    private fun generateIntentForUri(browser: Browser, uri: Uri): Intent = if (browser.supportsCustomTabs) {
        CustomTabsIntent.Builder().build().intent
    } else {
        Intent(Intent.ACTION_VIEW)
    }.apply {
        setPackage(browser.packageName)
        data = uri
    }

    private fun authorizationComplete(uri: Uri) {
        val response = Intent().apply { data = uri }
        setResult(RESULT_OK, response)
    }

    private fun authorizationCanceled() {
        val response = Intent()
        response.putExtra(OAUTH_EXCEPTION, OAuthError.UserCanceled)
        setResult(RESULT_CANCELED, response)
    }

    private fun noBrowserFound() {
        val response = Intent()
        response.putExtra(OAUTH_EXCEPTION, OAuthError.NoBrowserFound)
        setResult(RESULT_CANCELED, response)
    }

    private fun noUriFound() {
        val response = Intent()
        response.putExtra(OAUTH_EXCEPTION, OAuthError.NoURIFound)
        setResult(RESULT_CANCELED, response)
    }

    internal companion object {
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
        private const val KEY_AUTHORIZATION_STARTED = "authStarted"
    }
}

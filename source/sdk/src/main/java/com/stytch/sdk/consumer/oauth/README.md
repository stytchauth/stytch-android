# Package com.stytch.sdk.consumer.oauth
The [OAuth](OAuth.kt) interface provides methods for authenticating a user via a native Google OneTap prompt or any of the following third-party OAuth providers, provided you have configured them within your Stytch Dashboard:
- Amazon
- BitBucket
- Coinbase
- Discord
- Facebook
- Github
- GitLab
- Google
- LinkedIn
- Microsoft
- Salesforce
- Slack
- Twitch
- Yahoo

## Google OneTap
Google OneTap has been deprecated in favor of Google Credential Manager. To use it, you can call the `StytchClient.oauth.googleOneTap.start()` method, which will handle the entire authentication flow.

If this method returns an Error, it probably means your app is not configured for Google OneTap or there are no credentials available. You can fallback to a redirect-based OAuth flow by calling the `StytchClient.oauth.google.start()` method. 

## Third Party OAuth
In order to use Third-party OAuth flows, there are a few settings you need to configure in your application.

First, ensure you have added the relevant manifest placeholders in your application's `build.gradle` file (as explained in the [top-level README](/README.md)).

Second, you must specify a unique identifier for the activity result:
```kotlin
const val THIRD_PARTY_OAUTH_REQUEST = 456
```

Third, in your activity, add a listener for that unique identifier in `onActivityResult`, like so:
```kotlin
class MyActivity : AppCompatActivity() {
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            THIRD_PARTY_OAUTH_REQUEST -> data?.let { viewModel.authenticateThirdPartyOAuth(resultCode, it) }
        }
    }
}
```

To begin a Third-party OAuth flow, call the `StytchClient.oauth.[PROVIDER].start()` method, ensuring you pass in the unique third-party OAuth identifier defined previously. This will spawn a new (manager) activity that opens the user's default browser to begin the authentication flow. When the authentication flow has concluded, a new receiver activity intercepts the result, and returns it to the manager activity, which ultimately returns a response to your activity. This helps keep the backstack in order.

Once your listener has retrieved the `resultCode` and `Intent` from the activity listener, you can pass both to the `StytchClient.oauth.authenticate()` method to complete the authentication flow.

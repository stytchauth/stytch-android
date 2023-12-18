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
In order to use Google OneTap, you must register an activity result listener in your activity to listen for the intent returned by Google, which you will use to authenticate the request with Stytch.

First, define a unique identifier for the activity result, which you will use to both start the Google OneTap flow and listen for the activity result:
```kotlin
const val GOOGLE_OAUTH_REQUEST=123
```

Second, in your activity, add a listener for that unique identifier in `onActivityResult`, like so:
```kotlin
class MyActivity : AppCompatActivity() {
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GOOGLE_OAUTH_REQUEST -> data?.let { viewModel.authenticateGoogleOneTapLogin(it) }
        }
    }
}
```

Third, begin the Google OneTap flow by calling the `StytchClient.oauth.googleOneTap.start()` method making sure to provide the unique identifier created above.

Lastly, in your viewmodel for this example, you can authenticate the request by calling the `StytchClient.oauth.googleOneTap.authenticate()` method.

## Third Party OAuth
In order to use Third-party OAuth flows, there are a few settings you need to configure in your application.

First, ensure you have added the relevant manifest placeholders in your application's `build.gradle` file (as explained in the [top-level README](/README.md)).

Second, similar to the Google OneTap configuration, you must specify a unique identifier for the activity result:
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

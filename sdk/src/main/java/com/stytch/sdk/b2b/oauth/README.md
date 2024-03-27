# Package com.stytch.sdk.b2b.oauth
The [OAuth](OAuth.kt) interface provides methods for authenticating a user via a any of the following third-party OAuth providers, provided you have configured them within your Stytch Dashboard:
- Google
- Microsoft

## OAuth flows
In order to use provider OAuth flows, there are a few settings you need to configure in your application.

First, ensure you have added the relevant manifest placeholders in your application's `build.gradle` file (as explained in the [top-level README](/README.md)).

Second, you must specify a unique identifier for the activity result:
```kotlin
const val STYTCH_B2B_OAUTH_REQUEST = 456
```

Third, in your activity, add a listener for that unique identifier in `onActivityResult`, like so:
```kotlin
class MyActivity : AppCompatActivity() {
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            STYTCH_B2B_OAUTH_REQUEST -> data?.let { viewModel.authenticateB2BOAuth(resultCode, it) }
        }
    }
}
```

To begin a Provider OAuth flow, call wither the `StytchB2BClient.oauth.[PROVIDER].start()` or `StytchB2BClient.oauth.[PROVIDER].discovery.start()` methods, ensuring you pass in the unique OAuth identifier defined previously. This will spawn a new (manager) activity that opens the user's default browser to begin the authentication flow. When the authentication flow has concluded, a new receiver activity intercepts the result, and returns it to the manager activity, which ultimately returns a response to your activity. This helps keep the backstack in order.

Once you have retrieved the data from the returned intent, you can pass the required parameters to the `StytchB2BClient.oauth.authenticate()` or `StytchB2BClient.oauth.discovery.authenticate()` method, as appropriate, to complete the authentication flow.
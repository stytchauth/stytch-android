# OAuth / SSO
The Stytch Android SDK supports browser-based redirect flows for OAuth and SSO (B2B SDK only), as well as Native OAuth (Google OneTap/CredentialManager; Consumer SDK only)

The configuration necessary for each type of flow is different, so read on to see how to set each up.

To see all of the currently supported OAuth providers, check the properties of the [Consumer ](../source/sdk/src/main/java/com/stytch/sdk/consumer/oauth/OAuth.kt) and [B2B](../source/sdk/src/main/java/com/stytch/sdk/b2b/oauth/OAuth.kt) OAuth interfaces.

## Redirect
Redirect OAuth/SSO is the OAuth/SSO you may be most familiar with on the web: You click a button, are redirected to the selected IdP, login, and are redirected back to the original webpage. The same concept applies with the Stytch Android SDK, except the redirects are handled in a browser activity, and the backstack is managed by the SDK.

To accomplish this, the SDK uses a multi-activity pattern that handles all of the redirect and backstack management logic to ensure the backstack stays clean, and activities are finished in the correct order. If you were wondering why, in the getting started [README](../README.md), you needed to add manifest-placeholders for `stytchOAuthRedirectScheme` and `stytchOAuthRedirectHost`, it's to enable this functionality. Let's dig into those placeholders a little more.

When you provide these placeholders, the SDK registers an activity intent-filter for our "receiver" activity. When you start a redirect-based OAuth flow, the SDK launches a "manager" activity, which launches an "authorization" activity (the best system browser we can detect on device). Depending on the outcome of that authorization activity (user sign in, user canceled, etc), the authorization activity will either `finish` itself and return to the manager activity, or launch our receiver activity (identified by the manifest-placeholders), which will in turn launch our manager activity in such a way that ensures all previous activities in the flow are removed from the backstack.

For a deeper understanding of the third-party OAuth architecture, check out the [OAuth/SSO README](../source/sdk/src/main/java/com/stytch/sdk/common/sso/README.md).

There are two ways to listen for the result of this flow:
1. **Activity Result Callback -** Define an activity result callback in your activity, and use the appropriate `.start()` method. The manager activity will then report back to your activity, which you can listen for to authenticate the user and direct them as appropriate.
2. **Direct Token Capture -** Configure the OAuth/SSO client with your currently running activity (using `oauth.setOAuthReceiverActivity()`/`sso.setSSOReceiverActivity()`, and use the appropriate `getTokenForProvider()` method. Under the hood, this works the same way as the first option, but will return the final token to the coroutine/callback/CompletableFuture. This method is a little easier to setup, as you don't need to specify an activity result listener, or parse the returned link manually

### Example Code
For both flows in the following examples, we're going to use a redirect path of `my-app://oauth?type={}`, so make sure to add that as a valid redirect URL for the `Login` and `Signup` types in your Stytch Dashboard's [Redirect URL settings](stytch.com/dashboard/redirect-urls). You will also need to configure an [OAuth provider](https://stytch.com/dashboard/oauth). In this example, we are using GitHub.

Next, in your app's `build.gradle(.kts)`,  add the appropriate manifest placeholders:
```gradle
android {
    ...
    defaultConfig {
        ...
        manifestPlaceholders = [  
            'stytchOAuthRedirectScheme': 'my-app',
            'stytchOAuthRedirectHost': 'oauth'
        ]
    }
}
```
#### Activity Result Callback flow:
For this flow,  you will need to create an activity result handler in your main activity:
```kotlin
class MainActivity : FragmentActivity() {
    ...
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MyViewModel.STYTCH_OAUTH_REQUEST_CODE) {
            data?.let {
                viewModel.authenticateOAuthRequest(resultCode, it)
            }
        }
    }
}
```
Then, jump into the viewmodel to define your `start` and `authenticate` handlers:
```kotlin
class MyViewModel : ViewModel() {
    // you'll call this method from your activity and pass in the activity context in order to launch intents
    fun loginWithGitHub(context: Activity) {
        val startParameters =
            OAuth.ThirdParty.StartParameters(
                context = context,
                oAuthRequestIdentifier = STYTCH_OAUTH_REQUEST_CODE,
                // notice how we set the type here to identify between logins and signups
                loginRedirectUrl = "app://oauth?type=login",
                signupRedirectUrl = "app://oauth?type=signup",
            )
         StytchClient.oauth.github.start(startParameters)
    }

    fun authenticateOAuthRequest(
        resultCode: Int,
        intent: Intent,
    ) {
        if (resultCode == RESULT_OK) {
            intent.data?.let { url ->
                // you now have access to the returned URL, which contains the token to authenticate, as well as the "type" of request it was (login or signup)
                val token = url.getQueryParameter("token")
                val type = url.getQueryParameter("type")
                viewModelScope.launch {
                    val result = StytchClient.oauth.authenticate(
                        OAuth.ThirdParty.AuthenticateParameters(
                            token = token,
                            sessionDurationMinutes = 30
                        )
                    )
                    when (result) {
                        is StytchResult.Success -> {
                            // redirect the user based on the "type" parameter
                        }
                        is StytchResult.Error -> {}
                    }
                }
            }
        }
    }

    companion object {
        const val STYTCH_OAUTH_REQUEST_CODE = 555 // This can be whatever you want
    }
}
```
Put it all together by calling your start method from your activity:
```kotlin
viewModel.loginWithGitHub(this)
```

#### Direct Token Capture flow:
For the Direct Token Capture flow, you will need to configure the OAuth/SSO client with a compatible `ComponentActivity` (required for using the `registerForActivityResult` API) and use the `getTokenForProvider()` method (instead of `start()`).

First, configure the Stytch client from your calling activity:
```kotlin
class MainActivity : FragmentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
	    super.onCreate(savedInstanceState)
        StytchClient.oauth.setOAuthReceiverActivity(this)
        ...
    }
    override fun onDestroy() {  
	    super.onDestroy()  
	    StytchClient.oauth.setOAuthReceiverActivity(null)
	    ...
	}
	...
}
```

Second, configure your authentication flow in your viewmodel:
```kotlin
class MyViewModel : ViewModel() {
    fun loginWithGithub() {
        val startParameters =
            OAuth.ThirdParty.GetTokenForProviderParams(
                loginRedirectUrl = "app://oauth?type={}",
                signupRedirectUrl = "app://oauth?type={}",
            )
        viewModelScope.launch {
            when (val token = StytchClient.oauth.github.getTokenForProvider(startParameters)) {
                is StytchResult.Success -> {
                    val authenticateParameters =
                        OAuth.ThirdParty.AuthenticateParameters(
                            token = token.value,
                            sessionDurationMinutes = 30,
                        )
                    when (val result = StytchClient.oauth.authenticate(authenticateParameters)) {
                        is StytchResult.Success -> {
                            // OAuth authentication succeeded
                        }
                        is StytchResult.Error -> {
                            // OAuth authentication failed
                        }
                    }
                }
                is StytchResult.Error -> {
                    // OAuth start flow failed
                }
            }
        }
    }
}
```
Lastly, call the method from your UI:
```kotlin
viewmodel.loginWithGitHub()
```

## Native (Google OneTap/CredentialManager)
Native OAuth is a little bit different from, and quite a bit simpler than, Third Party OAuth, in which we use Google Credential Manager (formerly Google OneTap) to launch and authenticate an "OAuth ID Token" flow. After configuring your application in Google Developer Console (following the guides in the Stytch Dashboard), ensuring you have created _two_ client IDs (a "web" one, which Stytch uses to authenticate with Google, and an "Android" one, which your application uses to authenticate with Google), there is only one method you need to call:
```kotlin
fun loginWithGoogleOneTap(context: Activity) {
    viewModelScope.launch {
        val params = OAuth.GoogleOneTap.StartParameters(
            context = context,
            clientId = [YOUR_ANDROID_CLIENT_ID_FROM_GOOGLE_DEVELOPER_CONSOLE],
        )
        val result = StytchClient.oauth.googleOneTap.start(params)
        when (result) {
            is StytchResult.Success -> {
                // the user is authenticated!
                // use the value of `result.value.userCreated` to know whether they
                // created an account or logged in, and redirect accordingly
            }
            is StytchResult.Error -> {}
        }
    }
}
```

## Further Reading
For more information on the Stytch OAuth product, consult our [OAuth guide](https://stytch.com/docs/guides/oauth/idp-overview).
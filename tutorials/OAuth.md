# OAuth
The Stytch Android SDK supports two types of OAuth flows: Third Party (redirect) and Native (Google OneTap/CredentialManager), both of which can be configured in the Stytch Dashboard.

The configuration necessary for each type of flow is different, so read on to see how to set each up.

To see all of the currently supported providers, check the properties of the [OAuth interface](../source/sdk/src/main/java/com/stytch/sdk/consumer/oauth/OAuth.kt).

## Third Party (Redirect)
Third-party/Redirect OAuth is the OAuth you may be most familiar with on the web: You click a button, are redirected to the selected IdP, login, and are redirected back to the original webpage. The same concept applies with the Stytch Android SDK, except the redirects are handled in a browser activity, and the backstack is managed by the SDK.

To accomplish this, the SDK uses a multi-activity pattern that handles all of the redirect and backstack management logic to ensure the backstack stays clean, and activities are finished in the correct order. If you were wondering why, in the getting started [README](../README.md), you needed to add manifest-placeholders for `stytchOAuthRedirectScheme` and `stytchOAuthRedirectHost`, it's to enable this functionality. Let's dig into those placeholders a little more.

When you provide these placeholders, the SDK registers an activity intent-filter for our "receiver" activity. When you `start` a third-party OAuth flow, the SDK launches a "manager" activity, which launches an "authorization" activity (the best system browser we can detect on device). Depending on the outcome of that authorization activity (user sign in, user canceled, etc), the authorization activity will either `finish` itself and return to the manager activity, or launch our receiver activity (identified by the manifest-placeholders), which will in turn launch our manager activity in such a way that ensures all previous activities in the flow are removed from the backstack. The manager activity will then report an activity intent result back to your activity, which you can listen for to authenticate the user and direct them as appropriate.

For a deeper understanding of the third-party OAuth architecture, check out the [OAuth/SSO README](../source/sdk/src/main/java/com/stytch/sdk/common/sso/README.md).

### Example Code
For this example, we're going to use a redirect path of `my-app://oauth?type={}`, so make sure to add that as a valid redirect URL for the `Login` and `Signup` types in your Stytch Dashboard's [Redirect URL settings](stytch.com/dashboard/redirect-urls). You will also need to configure an [OAuth provider](https://stytch.com/dashboard/oauth). In this example, we are using GitHub.

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
Third, create the activity result handler in your main activity:

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
Fourth, jump into the viewmodel to define your `start` and `authenticate` handlers:
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
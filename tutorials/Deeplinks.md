# Deeplinks
Depending on the authentication flows you offer in your app (for instance: Email Magic Links or Password Reset By Email), you may need to configure Stytch and your application to handle deeplinks.

There are four steps to setting up deeplinks:
1. Adding your links as valid Redirect URLs in the [Stytch Dashboard](https://stytch.com/dashboard/redirect-urls)
2. Adding an intent filter to your activity in your application manifest
3. Adding an intent handler to your activity
4. Handling the actual deeplink, by passing the data to the appropriate Stytch product

## Step-By-Step
### Adding your links to Stytch
In order for your application to be notified of, and handle, a deeplink, you must tell the system what links you wish your app to handle. For simplicity, we're going to use `my-app://stytch-auth` for all of our deeplinks, so our Redirect URL table in the Stytch Dashboard would look something like this:
![Example of Redirect URL added to Stytch Dashboard](assets/deeplink-urls-stytch.png)

### Adding an intent filter
Next, in our manifest file, we're going to add an intent filter that uses that same scheme and host:
```xml
<manifest ...>
    <application ...>
	    <activity ...>
		    ...
		    <intent-filter android:label="@string/deep_link_title">
				<action android:name="android.intent.action.VIEW" />
				<category android:name="android.intent.category.DEFAULT" />
				<category android:name="android.intent.category.BROWSABLE" />
				<data android:scheme="my-app" android:host="stytch-auth" />
			</intent-filter>
	    </activity>
    </application>
</manifest>
```
### Adding an intent handler
At this point, your application is set up to handle any links that come in in the form of `myapp://stytch-auth`, so now we need to define how those are handled.

In your activity's `onCreate` method, you can read any incoming intent that has been passed to your app, and respond accordingly:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ...
    if (intent.action == Intent.ACTION_VIEW) {
	    intent.data?.let { deepLinkData ->
		    viewModel.handleDeeplink(deepLinkData)
	    }
    }
    ...
}
```
### Handling the deeplink
Once you have the deeplink data, you have two options for handling it: you can parse it manually, or you can let the `StytchClient.handle()` method do it for you. Manual parsing is not difficult, and gives you the most flexibility and fully-typed access to the response; `StytchClient.handle()` puts the burden on the Stytch SDK, but produces a more verbose object chain and can only guarantee a small subset of typed data (data that is common to all authentication requests).

We'll cover both approaches in this guide, and you can choose to use whichever works for you.

#### Manual Parsing
When you receive the `intent.data`, what you're getting back is just a URI object that can be parsed using standard Java URI methods. And since we know what the format of a Stytch Redirect URL looks like, we can easily parse out the data we need:
![Anatomy of a Stytch Redirect URL](assets/stytch-redirect-url-anatomy.svg)

In our viewmodel, lets handle the following deeplink URL: `my-app://stytch-auth?stytch_token_type=magic_links&token=some-encrypted-token`
```kotlin
fun handleDeeplink(uri: URI) {
	val tokenType = uri.getQueryParameter("stytch_token_type") ?:return
	val token = uri.getQueryParameter("token") ?: return
	// at this point, we know what the token and product is, and can
	// authenticate it directly (condensed for brevity)
	viewModelScope.launch {
		if (tokenType == "magic_links") {
			val result = StytchClient.magicLinks.authenticate(
				MagicLinks.AuthParameters(token, 30U)
			)
			when (result) {
				is StytchResult.Success -> {
					// user is authenticated!
				}
				is StytchResult.Error -> {
					// something went wrong
				}
			}
		}
	}
}
```
#### Automatic Parsing
If you don't want to parse out the token and token_type manually, you can pass the URI off to the SDK to handle. The tradeoff is that the response chain is a little more nested, and for authentication responses, can only guarantee the type to return a session_token and session_jwt. This may be enough for your use case, but is something to note.

Let's handle the same deeplink URI as in the previous section: `my-app://stytch-auth?stytch_token_type=magic_links&token=some-encrypted-token`
```kotlin
fun handleDeeplink(uri: URI) {
    viewModelScope.launch {
	    val result = StytchClient.handle(  
            uri = uri,  
			sessionDurationMinutes = sessionOptions.sessionDurationMinutes.toUInt(),  
		) 
	    when (result) {  
		    is DeeplinkHandledStatus.Handled -> {  
				// The deeplink has been fully parsed and will have a
				// nested response type that needs to be parsed
				when (val response = result.response) {
					is DeeplinkResponse.Auth -> {
						// This was a standard authentication response
						when (val authResponse = response.result) {
							is StytchResult.Success -> {
								// The user is authenticated!
								// authResponse will be guaranteed to have a session token and JWT, but that is all
							}
							is StytchResult.Error -> {
								// Something went wrong
							}
						}
					}
					is DeeplinkResponse.Discovery -> {
						// This is returned from our B2B Discovery flow
						// For consumer applications, you won't need to worry about it
					}
				}
		    }  
		    is DeeplinkHandledStatus.NotHandled -> {
			    // This was not a deeplink we know how to handle.
			    // It probably wasn't intended for us!
		    }  
		    is DeeplinkHandledStatus.ManualHandlingRequired -> {
			    // This is returned for things that need further processing,
			    // like Password Reset By Email flows.
			    // It will have the token type and token itself as properties
			    // so you can store them for later consumption
		    }  
		}
    }
}
```


## Further Reading
For more information on the structure of Redirect URLs, consult our [guide](https://stytch.com/docs/guides/dashboard/redirect-urls).
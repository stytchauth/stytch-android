![Stytch Android SDK](assets/Wordmark-dark-mode.png#gh-dark-mode-only)
![Stytch Android SDK](assets/Wordmark-light-mode.png#gh-light-mode-only)

![Test Status](https://github.com/stytchauth/stytch-android/actions/workflows/runOnGithub.yml/badge.svg)
![Android](https://img.shields.io/badge/Android-SDK23+-blue)

## Introduction
[Stytch](https://stytch.com) offers a comprehensive mobile authentication solution that simplifies integration with its API using our mobile SDKs. As the only authentication provider with a complete set of APIs, Stytch enables the creation of custom end-to-end authentication flows tailored to your mobile tech stack. With two integration options, `Stytch` and `StytchUI`, Stytch's SDKs allow you to craft an authentication experience that flexibility integrates into your app. `Stytch` offers a fully customizable headless API integration to suit your specific needs, while `StytchUI` provides a configurable view to expedite the integration process.

Note: Currently `StytchUI` only supports our consumer client, B2B UI coming soon! 

## Getting Started and SDK Installation
If you are completely new to Stytch, prior to using the SDK you will first need to visit [Stytch's homepage](https://stytch.com), sign up, and create a new project in the [dashboard](https://stytch.com/dashboard/home). You'll then need to adjust your [SDK configuration](https://stytch.com/dashboard/sdk-configuration) — adding your app's application id to `Authorized applications` and enabling any `Auth methods` you wish to use.

The Stytch Android SDK is distributed via Maven Central. To add the Stytch SDK, first ensure that you have added `mavenCentral()` to your projects `build.gradle(.kts)`:
```gradle
allprojects {
    ...
    repositories {
        ...
        mavenCentral()
    }
}
```
Then, add the Stytch SDK artifact to your application's dependencies:
```gradle
dependencies {
    ...
    implementation("com.stytch.sdk:sdk:latest")
    ...
}
```

Lastly, to enable OAuth deeplinks, you must modify your applications build.gradle(.kts) to supply two `manifestPlaceholders`. If you are not using our OAuth product, you still need to supply these placeholders, but they can be blank. These values can be any valid scheme or host, and do not relate to your OAuth settings in the Stytch Dashboard. These are only used internally within your app to register an OAuth receiver activity. More information is available in our [OAuth tutorial](./tutorials/OAuth.md).
```gradle
android {
    ...
    defaultConfig {
        ...
        manifestPlaceholders = [
            'stytchOAuthRedirectScheme': '[YOUR_AUTH_SCHEME]', // eg: 'app'
            'stytchOAuthRedirectHost': '[YOUR_AUTH_HOST]', // eg: 'myhost'
        ]
        ...
    }
}
```

## Configuration
Before using any part of the Stytch SDK, you must call configure to set the application context and public token as specified in your project dashboard.

If configuring from an Application Class:
``` kotlin
import com.stytch.sdk.consumer.StytchClient
class App : Application() {  
    override fun onCreate() {  
        super.onCreate()
        ...
        StytchClient.configure(  
            context = this,
            publicToken = [STYTCH_PUBLIC_TOKEN],
        )
        ...
    }
}
```

If configuring from an activity:
``` kotlin
import com.stytch.sdk.consumer.StytchClient
class MainActivity : FragmentActivity() {
    override fun onCreate() {  
        super.onCreate()
        ...
        StytchClient.configure(  
            context = applicationContext,
            publicToken = [STYTCH_PUBLIC_TOKEN],
        )
        ...
    }
}
```

## Stytch Usage
Stytch exposes clients for both Consumer and B2B, so make sure to use the one that corresponds with your project configuration. For the sake of this example we will be using the consumer one: StytchClient.

``` kotlin
import com.stytch.sdk.consumer.StytchClient

class MyViewModel : ViewModel() {
    // we'll be saving a method ID for later authentication
    private var methodId: String? = null

    // Send a OTP (one time passcode) via SMS
    fun sendSmsOtp(phoneNumber: String) {
        viewModelScope.launch {
            val response = StytchClient.otps.sms.loginOrCreate(  
                OTP.SmsOTP.Parameters(  
                    phoneNumber = phoneNumber,  
              ),  
            )
            when (response) {  
                is StytchResult.Success -> {
                    // save the methodId for the subsequent authenticate call 
                    methodId = response.value.methodId  
                }  
                is StytchResult.Error -> {  
                    // something went wrong  
                }  
            }
        }
    }

    // Authenticate a user using the OTP sent via SMS
    fun authenticateSmsOtp(code: String) {
        viewModelScope.launch {
            val response = StytchClient.otps.authenticate(
                OTP.SmsOTP.AuthParameters(
                    token = code,
                    methodId = methodId
              ),  
            )
            when (response) {  
                is StytchResult.Success -> {
                    // the user has been authenticated
                }  
                is StytchResult.Error -> {  
                    // something went wrong  
                }  
            }
        }
    }
}
```

## Further Stytch Usage
For further information and tutorials on some of our more common implementations, see the following:
* [Deeplinks](./tutorials/Deeplinks.md)
* [Email Magic Links](./tutorials/EmailMagicLinks.md)
* [OAuth](./tutorials/OAuth.md)
* [Passwords](./tutorials/Passwords.md)
* [Sessions](./tutorials/Sessions.md)

## StytchUI Usage
If you would like to use our pre-built UI SDK to get up and running as quickly as possible, replace the `sdk` artifact with the `ui` artifact in your application dependencies. The `ui` artifact bundles the `sdk` artifact, so you only need one or the other. And if you use the `ui` artifact, you can use _both_ the UI and headless SDK as your needs dictate.
```gradle
dependencies {
    ...
    implementation("com.stytch.sdk:ui:latest")
    ...
}
```
The UI SDK automatically handles all necessary OAuth, Email Magic Link, and Password Reset deeplinks. To enable this functionality, you need to add a specific redirect URL in your [Stytch Dashboard](https://stytch.com/dashboard/redirect-urls): `stytchui-[YOUR_PUBLIC_TOKEN]://deeplink`, and set it as valid for Signups, Logins, and Password Resets.

The Stytch UI uses the Builder pattern to initialize the UI, and allows you to fully customize the look, feel, and supported products.

Within your host activity, define the UI and any configuration options you would like. An example is below:
```kotlin
import com.stytch.sdk.ui.StytchUI

private val stytchUi = StytchUI.Builder().apply {
    activity(this@MyActivity)
    productConfig(
        StytchProductConfig(
            products = listOf(
                StytchProduct.OAUTH,
                StytchProduct.EMAIL_MAGIC_LINKS,
                StytchProduct.OTP,
                StytchProduct.PASSWORDS,
            ),
            // this is used for configuring Google OneTap specifically
            googleOauthOptions = GoogleOAuthOptions(
                clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
            ),
            oAuthOptions = OAuthOptions(
                loginRedirectURL = "uiworkbench://oauth", // This should match what you defined in your manifestPlaceholders
                signupRedirectURL = "uiworkbench://oauth", // This should match what you defined in your manifestPlaceholders
                providers = listOf(OAuthProvider.GOOGLE, OAuthProvider.APPLE, OAuthProvider.GITHUB)
            ),
            otpOptions = OTPOptions(
                methods = listOf(OTPMethods.SMS, OTPMethods.WHATSAPP),
            )
        )
    )
    // this handler is what is called when an authentication event (success, failure, or cancellation) occurs
    onAuthenticated {
        when (it) {
            is StytchResult.Success -> {
                Toast.makeText(this@MyActivity, "Authentication Succeeded", Toast.LENGTH_LONG).show()
            }
            is StytchResult.Error -> {
                Toast.makeText(this@MyActivity, it.exception.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}.build()
```
Then, simply call the `authenticate` method from your UI to launch the Stytch UI:
```kotlin
Button(modifier = Modifier.fillMaxSize(), onClick = stytchUi::authenticate) {
   Text("Launch Authentication")
}
```

The result of the authentication flow, either successful or not, will be reported to the `onAuthenticated` handler that you specified when building the StytchUI instance.

## Further Reading
Full reference documentation is available for [Stytch](https://stytchauth.github.io/stytch-android/sdk/index.html) and [StytchUI](https://stytchauth.github.io/stytch-android/ui/index.html).

## Get Help And Join The Community
Join the discussion, ask questions, and suggest new features in our ​[Slack community](https://stytch.com/docs/resources/support/overview)!

Check out the [Stytch Forum](https://forum.stytch.com/) or email us at [support@stytch.com](mailto:support@stytch.com).

## License
The Stytch Android SDK is released under the MIT license. See [LICENSE](LICENSE) for details.

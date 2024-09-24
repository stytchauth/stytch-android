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
    implementation("com.stytch.sdk:sdk:latest.release")
    ...
}
```

Lastly, you must modify your applications build.gradle(.kts) to supply three `manifestPlaceholders`; two of them are for enabling OAuth deeplinks, and one is for enabling our UI SDK. If you are not using either, you still need to supply these placeholders, but they can be blank. The OAuth manifest placeholder values can be any valid scheme or host, and do not relate to your OAuth settings in the Stytch Dashboard. These are only used internally within your app to register an OAuth receiver activity. More information is available in our [OAuth tutorial](./tutorials/OAuth.md). The STYTCH_PUBLIC_TOKEN is your public token, which you can get from your project dashboard
```gradle
android {
    ...
    defaultConfig {
        ...
        manifestPlaceholders = [
            'stytchOAuthRedirectScheme': '[YOUR_AUTH_SCHEME]', // eg: 'app'
            'stytchOAuthRedirectHost': '[YOUR_AUTH_HOST]', // eg: 'myhost'
            'STYTCH_PUBLIC_TOKEN': '[STYTCH_PUBLIC_TOKEN]',
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
                OTP.AuthParameters(
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
### Concurrency
While the Stytch Android SDK makes heavy use of Coroutines under the hood, every suspend function has a callback-compatible version for developers that are not using Coroutines. An example of the above `authenticateSmsOtp` method with callbacks might look like this:
```kotlin
fun authenticateSmsOtp(code: String) {
    val params = OTP.AuthParameters(
        token = code,
        methodId = methodId
    )
    StytchClient.otps.authenticate(params) { response ->
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
```

## Further Stytch Usage
For further information and tutorials on some of our more common implementations, see the following:
* [Deeplinks](./tutorials/Deeplinks.md)
* [Email Magic Links](./tutorials/EmailMagicLinks.md)
* [OAuth](./tutorials/OAuth.md)
* [Passwords](./tutorials/Passwords.md)
* [Sessions](./tutorials/Sessions.md)
* [StytchUI](./tutorials/UI.md)

## Further Reading
Full reference documentation is available for [Stytch](https://stytchauth.github.io/stytch-android/sdk/index.html) and [StytchUI](https://stytchauth.github.io/stytch-android/ui/index.html).

## Navigating the Project and Running the Sample Apps
This repository is organized in three main parts:
* **workbench-apps/** - These are testing apps, intended for internal development purposes. _Almost_ all user flows are implemented in these apps, for reference and testing, but do not necessarily represent best practices or realistic usage.
* **example-apps/** - These are two example apps (one in Kotlin, one in Java), demonstrating realistic use cases of the Stytch SDK, using both the Headless and Pre-Built UI implementations. Feel free to copy these projects and edit them to suit your needs
* **source/sdk/** - This is the actual source code of the Stytch Android SDK

If you wish to run any of the example or workbench apps from within this repository, you should add some, or all, of the following properties to your `local.properties` file:
* **STYTCH_PUBLIC_TOKEN** - Your Consumer Project public token. Used in both of the example apps and the consumer workbench app
* **GOOGLE_OAUTH_CLIENT_ID** - A Google OAuth client ID, created in your Google Console (linked to `com.stytch.exampleapp`) and added to your Stytch Dashboard, used in the consumer workbench app for testing Google One Tap
* **STYTCH_B2B_PUBLIC_TOKEN** - Your B2B Project public token. Used in the B2B workbench app
* **STYTCH_B2B_ORG_ID** - The ID of one of your B2B organizations. This is used as a convenience property in the B2B workbench app, so you don't have to type it in manually on your device
* **UI_GOOGLE_CLIENT_ID** - A Google OAuth client ID, created in your Google Console (linked to `com.stytch.uiworkbench`) and added to your Stytch Dashboard, used in the UI workbench app for testing Google One Tap
* **PASSKEYS_DOMAIN** - The domain where you host your `/.well-known/assetlinks.json` file, used in the consumer workbench app to test Passkeys flows

If you do not add these properties, the applications should still build, but will not function as expected.

## Get Help And Join The Community
Join the discussion, ask questions, and suggest new features in our ​[Slack community](https://stytch.com/docs/resources/support/overview)!

Check out the [Stytch Forum](https://forum.stytch.com/) or email us at [support@stytch.com](mailto:support@stytch.com).

## License
The Stytch Android SDK is released under the MIT license. See [LICENSE](LICENSE) for details.

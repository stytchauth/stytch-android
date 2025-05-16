![Stytch Android SDK](assets/Wordmark-dark-mode.png#gh-dark-mode-only)
![Stytch Android SDK](assets/Wordmark-light-mode.png#gh-light-mode-only)

![Test Status](https://github.com/stytchauth/stytch-android/actions/workflows/runOnGithub.yml/badge.svg)
![Android](https://img.shields.io/badge/Android-SDK23+-blue)

## Introduction
[Stytch](https://stytch.com) offers a comprehensive mobile authentication solution that simplifies integration with its API using our mobile SDKs. As the only authentication provider with a complete set of APIs, Stytch enables the creation of custom end-to-end authentication flows tailored to your mobile tech stack. With two integration options, `Stytch` and `StytchUI`, Stytch's SDKs allow you to craft an authentication experience that flexibility integrates into your app. `Stytch` offers a fully customizable headless API integration to suit your specific needs, while `StytchUI` provides a configurable view to expedite the integration process.

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

Lastly, add a new string resource called `STYTCH_PUBLIC_TOKEN` with the value of your public token from the Stytch Dashboard. This will automatically enable the necessary activities for handling [OAuth deeplinks](./tutorials/OAuth.md) and our pre-built UI components, as well as automatic configuration of the Stytch client:
```xml
<resources>
    <string name="STYTCH_PUBLIC_TOKEN">YOUR_PUBLIC_TOKEN</string>
</resources>
```

## Configuration
Before using any part of the Stytch SDK, you must call configure to set the application context, which is used for retrieving the public token and registering necessary activities/receivers/deeplinks.

If configuring from an Application Class:
``` kotlin
import com.stytch.sdk.consumer.StytchClient
class App : Application() {  
    override fun onCreate() {  
        super.onCreate()
        ...
        StytchClient.configure(this)
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
        StytchClient.configure(applicationContext)
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
* [UI Custom Copy](./tutorials/Localization.md)

## Further Reading
Full reference documentation is available for [Stytch](https://stytchauth.github.io/stytch-android/sdk/index.html) and [StytchUI](https://stytchauth.github.io/stytch-android/ui/index.html).

## Navigating the Project and Running the Sample Apps
This repository is organized in three main parts:
* **workbench-apps/** - These are testing apps, intended for internal development purposes. _Almost_ all user flows are implemented in these apps, for reference and testing, but do not necessarily represent best practices or realistic usage.
* **example-apps/** - These are two example apps (one in Kotlin, one in Java), demonstrating realistic use cases of the Stytch SDK, using both the Headless and Pre-Built UI implementations. Feel free to copy these projects and edit them to suit your needs
* **source/sdk/** - This is the actual source code of the Stytch Android SDK

For both the example apps and the workbench apps, you should consider them the same as if you were configuring your own custom app, and follow the setup instructions from above in regards to adding a string resource containing your `STYTCH_PUBLIC_TOKEN`. The `consumer-workbench` app also accepts the two following additional string resources:
1. `PASSKEYS_DOMAIN` - for associating the workbench app with a domain on which you host your `.well-known/assetlinks.json` file
2. `GOOGLE_CLIENT_ID` - for configuring Google OneTap

In order to run these example and workbench apps, you will need to set the following allowed Application IDs in your Stytch Dashboard:
* **example-apps/javademoapp:** `com.stytch.javademoapp`
* **example-apps/stytchexampleapp:** `com.stytch.stytchexampleapp`
* **workbench-apps/b2b-workbench:** `com.stytch.exampleapp.b2b`
* **workbench-apps/stytchexampleapp:** `com.stytch.exampleapp`

## Get Help And Join The Community
Join the discussion, ask questions, and suggest new features in our ​[Slack community](https://stytch.com/docs/resources/support/overview)!

Check out the [Stytch Forum](https://forum.stytch.com/) or email us at [support@stytch.com](mailto:support@stytch.com).

## License
The Stytch Android SDK is released under the MIT license. See [LICENSE](LICENSE) for details.

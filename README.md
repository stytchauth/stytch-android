![Stytch Android SDK](assets/Wordmark-dark-mode.png#gh-dark-mode-only)
![Stytch Android SDK](assets/Wordmark-light-mode.png#gh-light-mode-only)

![Language][language-shield]
[![Release][release-shield]][release-url]
[![MIT License][license-shield]][license-url]
[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]

## Table of Contents
* [Getting Started](#getting-started)
    * [What is Stytch?](#what-is-stytch)
    * [Why should I use the Stytch SDK?](#why-should-i-use-the-stytch-sdk)
    * [What can I do with the Stytch SDK?](#what-can-i-do-with-the-stytch-sdk)
        * [Consumer Applications](#for-consumer-applications)
        * [B2B Applications](#for-b2b-applications)
        * [Async Options](#async-options)
    * [How do I start using Stytch?](#how-do-i-start-using-stytch)
* [Requirements](#requirements)
* [Installation and setup](#installation-and-setup)
    * [Consumer Applications](#for-consumer-applications-1)
    * [B2B Applications](#for-b2b-applications-1)
* [Usage](#usage)
    * [Deeplink Handling](#deeplink-handling)
    * [Example app](#example-app)
    * [Authenticating](#authenticating)
* [Documentation](#documentation)
* [FAQ](#faq)
* [Questions?](#questions)
* [License](#license)

## Getting Started

### What is Stytch?

[Stytch](https://stytch.com) is an authentication platform, written by developers for developers, with a focus on
improving security and user experience via passwordless authentication. Stytch offers direct API integrations,
language-specific libraries, and SDKs (like this one) to make the process of setting up an authentication flow for your
app as easy as possible.

### Why should I use the Stytch SDK?

Stytch's SDKs make it simple to seamlessly onboard, authenticate, and engage users. The Android SDK provides the easiest
way for you to use Stytch on Android. With just a few lines of code, you can easily authenticate your users and get back
to focusing on the core of your product.

### What can I do with the Stytch SDK?

There are a number of authentication products currently supported by the SDK, with additional functionality coming in
the near future! The full list of currently supported products is as follows:

#### **For Consumer Applications:**
- Magic links
    - Send/authenticate magic links via Email
- OTPs
    - Send/authenticate one-time passcodes via SMS, WhatsApp, Email
- Passwords
    - Create or authenticate a user
    - Check password strength
    - Reset a password
- Sessions
    - Authenticate/refresh an existing session
    - Revoke a session (Sign out)
- Biometrics
    - Register/authenticate with biometrics
- OAuth
    - Register/authenticate with native Google OneTap
    - Register/authenticate with our supported third-party OAuth providers (Amazon, BitBucket, Coinbase, Discord, Facebook, Github, GitLab, Google, LinkedIn, Microsoft, Slack, or Twitch)
- User Management
    - Get or fetch the current user object (sync/cached or async options available)
    - Delete factors by id from the current user

#### **For B2B Applications:**
- Magic links
    - Send/authenticate magic links via Email
- Sessions
    - Authenticate/refresh an existing session
    - Revoke a session (Sign out)
- Members
    - Get or fetch the current user object (sync/cached or async options available)
- Organizations
    - Get or fetch the current user's organization

#### **Async Options**

The SDK provides several different mechanisms for handling the asynchronous code, so you can choose what best suits your
needs.

- `Coroutines`
- `Callbacks`

### How do I start using Stytch?

If you are completely new to Stytch, prior to using the SDK you will first need to
visit [Stytch's homepage](https://stytch.com), sign up, and create a new project in
the [dashboard](https://stytch.com/dashboard/home). You'll then need to adjust
your [SDK configuration](https://stytch.com/dashboard/sdk-configuration) — adding your app's applicationId
to `Authorized environments` and enabling any `Auth methods` you wish to use.

## Requirements

This SDK supports Android API level 23 and
above ([distribution stats](https://developer.android.com/about/dashboards/index.html))

## Installation and setup

Add the Stytch dependency to your `app/build.gradle` file:

```implementation 'com.stytch.sdk:sdk:latest'```

### Getting app secrets ready

1. Go to https://stytch.com/dashboard, and sign up/log in with your email address.

2. Once you are on the dashboard, click on the "API Keys" tab on the left. Scroll down to the "Public tokens" section
   and copy your public token.

3. In your android app, before you call any other part of the Stytch SDK, you must first call
   the `StytchClient.configure` function and pass in your applicationContext and public token:

#### **For Consumer Applications:**
```kotlin
import com.stytch.sdk.consumer.StytchClient

StytchClient.configure(
    context = application.applicationContext,
    publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN
)
```
#### **For B2B Applications:**
```kotlin
import com.stytch.sdk.b2b.StytchB2BClient

StytchB2BClient.configure(
    context = application.applicationContext,
    publicToken = BuildConfig.STYTCH_PUBLIC_TOKEN
)
```

## Usage

#### Deeplink Handling

This example shows a hypothetical Android files, with deeplink/universal link handling.

#### MainActivity.kt

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // omitted

    if (intent.action == Intent.ACTION_VIEW) {
        handleIntent(intent)
    }
}

private fun handleIntent(intent: Intent) {
    intent.data?.let { appLinkData ->
        viewModel.handleUri(appLinkData)
    }
}
```

#### MainViewModel.kt

```kotlin
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.responseData.BasicData
import com.stytch.sdk.consumer.StytchClient

lateinit var result: StytchResult<BasicData>

fun handleUri(uri: Uri) {
    viewModelScope.launch {
        result = StytchClient.handle(uri = uri, sessionDurationMinutes = 60u)
    }
}
```

#### AndroidManifest.xml

```xml

<activity android:name="com.stytch.exampleapp.MainActivity"
          android:exported="true">
    <!--omitted-->
    <intent-filter android:label="@string/deep_link_title">
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data
            android:scheme="app"
            android:host="@string/host"
            android:pathPrefix="/"/>
    </intent-filter>
</activity>
```

With the above in place your app should be ready to accept deeplinks

### Example app

The above code is used in practice in the Example Apps, which can be run and used to test out various flows of the SDK.
In order to run the application, you have to define some gradle properties in your global or local `gradle.properties`.
#### **For Consumer Applications**
- `STYTCH_PUBLIC_TOKEN` - This token can be retrieved from your Stytch dashboard as mentioned before in this README.
- `GOOGLE_OAUTH_CLIENT_ID` - This client ID is configured in your Google OAuth settings. You can leave it blank if you are not testing Google OneTap
#### **For B2B Applications**
- `STYTCH_B2B_PUBLIC_TOKEN` - This token can be retrieved from your Stytch dashboard as mentioned before in this README.
- `STYTCH_B2B_ORG_ID` - You must create an organization in your Stytch product, and retrieve this ID

### Authenticating

As seen in [What can I do with the Stytch SDK?](#what-can-i-do-with-the-stytch-sdk), there are a number of different
authentication products available. Here, we'll showcase a simple example of using the OTP product.

#### One-time Passcodes

This example shows a hypothetical function you could use to use SMS authentication in your app, delegating much of the
work to the StytchClient under the hood.

```kotlin
import com.stytch.sdk.common.StytchResult
import com.stytch.sdk.common.network.responseData.BasicData
import com.stytch.sdk.consumer.StytchClient

lateinit var result: StytchResult<BasicData>

fun loginOrCreateSMS() {
    if (phoneNumberIsValid) {
        viewModelScope.launch {
            result = StytchClient.otps.sms.loginOrCreate(OTP.SmsOTP.Parameters(phoneNumberTextState.text))
        }
    } else {
        showPhoneError = true
    }
}
```

## Documentation

Full reference documentation is available [here](https://stytchauth.github.io/stytch-kotlin/).

## FAQ

Q.How does the SDK compare to the API?

A.The SDK, for the most part, mirrors the API directly — though it provides a more opinionated take on interacting with these methods; managing local state on your behalf and introducing some defaults (viewable in the corresponding init/function reference docs). A primary benefit of using the SDK is that you can interact with Stytch directly from the client, without relaying calls through your backend. 

Q. What are the some of the default behaviors of the SDK? 

A. A few things here: 1) the session token/JWT will be stored in/retrieved from the system encrypted storage, so will safely persist across app launches. 2) The session and user objects are not cached by the SDK, these must be pulled from the `authenticate` responses and stored by the application. 3) After a successful authentication call, the SDK will begin polling in the background to refresh the session and its corresponding JWT, to ensure the JWT is always valid (the JWT expires every 5 minutes, regardless of the session expiration.)

Q. Are there guides or sample apps available to see this in use?

A. Yes! There is a Demo App included in this repo, available [here (consumer application)](consumerExampleapp) and [here (B2B application)](b2bExampleapp).

### Questions?

Feel free to reach out any time at [support@stytch.com](mailto:support@stytch.com), our [Slack](https://join.slack.com/t/stytch/shared_invite/zt-nil4wo92-jApJ9Cl32cJbEd9esKkvyg), or our [forums](https://forum.stytch.com/).

## License

The Stytch Android SDK is released under the MIT license. See [LICENSE](LICENSE) for details.

[contributors-shield]: https://img.shields.io/github/contributors/stytchauth/stytch-kotlin.svg?style=for-the-badge
[contributors-url]: https://github.com/stytchauth/stytch-kotlin/graphs/contributors
[issues-shield]: https://img.shields.io/github/issues/stytchauth/stytch-kotlin.svg?style=for-the-badge
[issues-url]: https://github.com/stytchauth/stytch-kotlin/issues
[license-shield]: https://img.shields.io/github/license/stytchauth/stytch-kotlin.svg?style=for-the-badge
[license-url]: https://github.com/stytchauth/stytch-kotlin/blob/master/LICENSE
[release-shield]: https://img.shields.io/github/v/release/stytchauth/stytch-kotlin?style=for-the-badge
[release-url]:https://github.com/stytchauth/stytch-kotlin/releases
[language-shield]: https://img.shields.io/github/languages/top/stytchauth/stytch-kotlin?style=for-the-badge

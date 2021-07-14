# Stytch Android SDK

## Table of contents

* [Overview](#overview)
* [Requirements](#requirements)
* [Dependency](#dependency)
* [Getting Started](#getting-started)
  * [Pre-built UI](#pre-built-ui)
    * [Email Magic Link Authentication](#email-magic-link-authentication)
    * [SMS Passcode Authentication](#sms-passcode-authentication)
  * [Direct API](#direct-api)

## Overview

Stytch's Android SDK makes it simple to sign up new users and log in existing users to your service. Passwordless authentication provides improved security, a better user experience, and better user retention than traditional username and password authentication.

With this SDK, you can use one of our elegant prebuilt UI flows (that offer extensive customization options), our use the Stytch API directly and build your own UI for maximum control.

## Requirements

This SDK supports Android API level 21 and above ([distribution stats](https://developer.android.com/about/dashboards/index.html))

## Dependency

Add the Stytch dependency to your app/build.gradle file:

`implementation 'com.stytch.sdk:sdk:0.3.0'`

## Getting Started

1. Go to https://stytch.com/dashboard, and sign up/log in with your email address.

2. Once you are on the dashboard, click on the "API Keys" tab on the left. Scroll down to the "Public tokens" section and copy your public token.

3. In your android app, before you can any other part of the Stytch SDK, you must first call the `Stytch.configure` function and pass in your public token:

```
import com.stytch.sdk.Stytch
import com.stytch.sdk.StytchEnvironment

...

Stytch.configure(
    publicToken = public-token-test-792e8013-4a7c-4d7c-848f-9fc94fc8ba73, // Replace with your public token
    environment = StytchEnvironment.TEST,
)
```

## Pre-built UI

This SDK offers currently offers two beautiful pre-built UI flows: `StytchUI.EmailMagicLink` and `StytchUI.SMSPasscode` (with more coming soon!)

(If you instead want to build your own UI from scratch, see the [Direct API](#direct-api) section below.)

In either case, before setting up these UI flows, you can customize how the Stytch UI looks by optionally setting the `StytchUI.uiCustomization` variable. (If you do not set this variable, the default Stytch theme will be used.)

To do so, create an instance of the `StytchUICustomization` class:

```
import com.stytch.StytchUI
import com.stytch.StytchUICustomization

...

StytchUI.uiCustomization = StytchUICustomization(
    // customize attributes here
)
```

This is a full list of all the attributes you can customize:

StytchUICustomization
- `backgroundColor: StytchColor` - Window background color
- `hideActionBar: Boolean` - If true, no toolbar/action bar will be shown at top of screen
- `actionBarColor: StytchColor` - Toolbar/Action bar color
- `titleStyle: StytchTextStyle` - Title text style
- `subtitleStyle: StytchTextStyle` - Subtitle text style
- `consentTextStyle: StytchTextStyle` - (SMS Passcode flow only) SMS consent text style
- `inputTextStyle: StytchTextStyle` - User input (email/phone number/passcode) text style
- `inputHintStyle: StytchTextStyle` - Hint text style
- `inputBackgroundColor: StytchColor` - Background color of input boxes
- `inputCornerRadius: DensityIndependentPixels` - Radius of rounded corners of input boxes, in [dp](https://developer.android.com/training/multiscreen/screendensities)
- `buttonTextStyle: StytchTextStyle` - Button text style
- `buttonDisabledTextColor: StytchColor` - Set a different text color when buttons are in the disabled state
- `buttonEnabledBackgroundColor: StytchColor` - Button background color when in enabled state
- `buttonDisabledBackgroundColor: StytchColor` - Button background color when in disabled state
- `buttonCornerRadius: DensityIndependentPixels` - Radius of rounded corners of button, in [dp](https://developer.android.com/training/multiscreen/screendensities)
- `errorTextStyle: StytchTextStyle` - Error message text style

StytchTextStyle
- `size: ScalablePixels` - Text size in [sp](https://developer.android.com/training/multiscreen/screendensities)
- `color: StytchColor` - Text Color
- `font: StytchFont` - Text font

After customizing the UI to your liking, the next step is to configure the UI flow of your choice:
- [Email Magic Link](#email-magic-link-authentication)
- [SMS Passcode](#sms-passcode-authentication)

## Email Magic Link Authentication

In order to handle the deeplink from an emailed magic link, you have to add an intent filter to `StytchEmailMagicLinkActivity` to recognize the deeplink.

To do so, add the following to your `AndroidManifest.xml`, nested under the `application` tag:

```
<activity
    android:name="com.stytch.sdk.StytchEmailMagicLinkActivity"
    android:launchMode="singleTask"
    >

    <intent-filter android:autoVerify="true">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="https"
        android:host="example.com" />
    </intent-filter>
</activity>
```

Replace 'example.com' with your company's domain.

We highly recommend setting up [Android App Link auto-verification](https://developer.android.com/training/app-links#add-app-links) for the best user experience. Without auto-verification, deeplinks will still work, but your users will have to tap on a dialog to choose your app over the browser instead of automatically being sent to your app.

Next, back in your code, you need to configure the Email Magic Link UI flow before you can launch it:

```
import com.stytch.sdk.StytchUI

...

StytchUI.EmailMagicLink.configure(
    // deeplink for logging in an existing user
    loginMagicLinkUrl = "https://YOUR_DOMAIN/login",

    // deeplink for creating a new user
    signupMagicLinkUrl = "https://YOUR_DOMAIN/signup",

    // if true, will create new users in a "pending" state until they actually open the magic link
    createUserAsPending = true,

    // For security reasons, the Stytch SDK does not authenticate magic link tokens directly,
    // and we strongly recommend that you do not authenticate tokens from your app either.
    // Instead, we recommend that you send the token to your own backend,
    // And call the Stytch API to authenticate it from there.
    // This is a callback for receiving the token from the Stytch SDK.
    authenticator = { token ->
        // From here, send the token to your backend to authenticate.
        // After you have successfully (or unsuccessfully) completed authentication,
        // Call either 'StytchUI.onTokenAuthenticated()' to close the Stytch UI,
        // Or call 'StytchUI.onTokenAuthenticationFailed()' to show an error message to the user.
    }
)
```

Finally, launch the Stytch activity:

```
import com.stytch.sdk.StytchUI

...

// Inside your activity class
val intent = StytchUI.EmailMagicLink.createIntent(this)
startActivity(intent)
```

## SMS Passcode Authentication

First, configure the SMS Passcode UI flow:

```
import com.stytch.sdk.StytchUI

...

StytchUI.SMSPasscode.configure(
    // if true, will create new users in a "pending" state until the passcode is verified
    createUserAsPending = true,

    // For security reasons, the Stytch SDK does not authenticate passcodes (tokens) directly,
    // and we strongly recommend that you do not authenticate tokens from your app either.
    // Instead, we recommend that you send the token to your own backend,
    // And call the Stytch API to authenticate it from there.
    // This is a callback for receiving the token from the Stytch SDK.
    authenticator = { methodId, token ->
        // From here, send the token to your backend to authenticate.
        // After you have successfully (or unsuccessfully) completed authentication,
        // Call either 'StytchUI.onTokenAuthenticated()' to close the Stytch UI,
        // Or call 'StytchUI.onTokenAuthenticationFailed()' to show an error message to the user.
    }
)
```

Then, launch the Stytch activity:

```
import com.stytch.sdk.StytchUI

...

// Inside your activity class
val intent = StytchUI.SMSPasscode.createIntent(this)
startActivity(intent)
```

## Direct API

If you want to build your own UI from scratch, you can still authenticate users using either passwordless method (email magic link or SMS passcode) by using the Stytch direct API.

Email Magic Link example:

```
import kotlinx.coroutines.GlobalScope
import com.stytch.sdk.StytchApi

...

GlobalScope.launch {
    val result = StytchApi.MagicLinks.Email.loginOrCreate(
        email = "[user email]",
        loginMagicLinkUrl = "[your url]/login",
        signupMagicLinkUrl = "[your url]/signup",
    )
    // handle result here
}
```

Alternatively, if you do not use Kotlin coroutines in your codebase, you can instead provide an asynchronous callback by using the `StytchCallbackApi` object:

```
import com.stytch.sdk.StytchCallbackApi

...

StytchCallbackApi.MagicLinks.Email.loginOrCreate(
    email = "[user email]",
    loginMagicLinkUrl = "[your url]/login",
    signupMagicLinkUrl = "[your url]/signup",
) { result ->
    // handle result here
}
```

SMS Passcode example:

```
import kotlinx.coroutines.GlobalScope
import com.stytch.sdk.StytchApi

...

GlobalScope.launch {
    val result = StytchApi.MagicLinks.Email.loginOrCreate(
        phoneNumber = "[user phone number]",
    )
    // handle result here
}
```

Alternatively, if you do not use Kotlin coroutines in your codebase, you can instead provide an asynchronous callback by using the `StytchCallbackApi` object:

```
import com.stytch.sdk.StytchCallbackApi

...

StytchCallbackApi.MagicLinks.Email.loginOrCreate(
    phoneNumber = "[user phone number]",
) { result ->
    // handle result here
}
```

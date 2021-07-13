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

(If you instead want to build your own UI from scratch, see the [Direct Api](#direct-api) section below.)

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

TODO

## SMS Passcode Authentication

TODO

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

-------------------------------------------------

# Stytch Android SDK

## Table of contents

* [Overview](#overview)
* [Requirements](#requirements)
* [Installation](#installation)
* [Getting Started](#getting-started)
  * [Configuration](#configuration)
  * [Starting UI Flow](#starting-ui-flow)
  * [Starting Custom Flow](#starting-custom-flow)

## Overview

Stytch's SDKs make it simple to seamlessly onboard, authenticate, and engage users. Improve security and user experience with passwordless authentication.

## Permissions

Open your app's `AndroidManifest.xml` file and add the following permission.

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Requirements

The SDK supports API level 21 and above ([distribution stats](https://developer.android.com/about/dashboards/index.html)).

- minSdkVersion = 21
- Android Gradle Plugin 4.1.1
- AndroidX

## Installation

```gradle
repositories {
   maven()
}
```

Add stytch-android to your build.gradle dependencies.

```app-gradle

android {
      compileOptions {
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
      }
      kotlinOptions {
            jvmTarget = '1.8'
      }
}
            
dependencies {
    implementation 'com.stytch.sdk:sdk:0.1.2'
}
```

## Getting Started

### Configuration

Pick a unique URL scheme for redirecting the user back to your app.
For this example, we'll use YOUR_APP_NAME://.
To start using Stytch, you must configure it:

```
    Stytch.instance.configure(
        PROJECT_ID,
        SECRET,
        YOUR_APP_NAME,
        HOST
    )
```

You can specify Stytch environment `TEST` or `LIVE`:

```
    Stytch.instance.environment = StytchEnvironment.TEST
```

You can specify Stytch loginMethod `LoginOrSignUp` (default) or `LoginOrInvite`:
`LoginOrSignUp`  - Send either a login or sign up magic link to the user based on if the email is associated with a user already. 
`LoginOrInvite` - Send either a login or invite magic link to the user based on if the email is associated with a user already. If an invite is sent a user is not created until the token is authenticated. 
```
Stytch.instance.loginMethod = StytchLoginMethod.LoginOrInvite
```

Add this in your AndroidManifest.xml.
For StytchUI ACTIVITY_NAME = "com.stytch.sdk.ui.StytchMainActivity"

```
<activity android:name="ACTIVITY_NAME"
    android:theme="@style/Theme.StytchTheme"
    android:launchMode="singleTask">

    <intent-filter android:label="stytch_sdk_deep_link">
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme=<YOUR_APP_SCHEME>
            android:host="stytch.com" />
    </intent-filter>
</activity>
```

### Starting UI Flow

#### Show UI

Call StytchUI instance showUI with Activity/AppCompatActivity/Fragment, StytchUI.StytchUIListener & optional parameter StytchUICustomization

```
    StytchUI.instance.showUI(
        this,
        this,
        createCustomization()
    )
```

#### UI Customization

StytchUICustomization() creates Default Customization, to change theme use its parameters

StytchUICustomization
- `titleStyle: StytchTextStyle` - Title text style
- `showTitle: Boolean` - Show/hide title
- `subtitleStyle: StytchTextStyle` - Subtitle text style
- `showSubtitle: Boolean` - Show/hide subtitle
- `inputCornerRadius: Float` - Input corner radius, size in pixels
- `inputBackgroundBorderColorId: Int` - Input border color
- `inputBackgroundColorId: Int` - Input background color
- `inputHintStyle : StytchTextStyle` - Input hint text style
- `inputTextStyle: StytchTextStyle` - Input text style
- `buttonTextStyle: StytchTextStyle` - Action button text style
- `buttonBackgroundColorId : Int` - Action button background color
- `buttonCornerRadius: Float` - Action button corner radius, size in pixels
- `showBrandLogo: Boolean` - Show/hide brand logo
- `backgroundId : Int` - Window background color

StytchTextStyle
- `size: Float` - Text size in pixels
- `colorId: Int` - Text Color
- `font: Typeface?` - Text font

##### Handle UI Callbacks

StytchUI.StytchUIListener provides callbacks methods
- `onEvent` - called after user found or created
- `onSuccess` - calls after successful user authorization
- `onFailure` - called when invalid configuration

```
StytchUI.StytchUIListener{
    override fun onSuccess(result: StytchResult) {
        Log.d(TAG,"onSuccess: $result")
    }

    override fun onFailure() {
        Log.d(TAG,"onFailure: Oh no")
    }
    
    override fun onEvent(event: StytchEvent) {
        Log.d(TAG,"Event Type: ${event.type}")
        Log.d(TAG,"Is user created: ${event.created}")
        Log.d(TAG,"User ID: ${event.userId}")
    }
}
```

### Starting Custom Flow


Handle deep link Intent with Stytch.instance.handleDeepLink
```
override fun onNewIntent(intent: Intent?) {
    val action: String? = intent?.action
    val data = intent?.data ?: return

    if (action == Intent.ACTION_VIEW) {
        if(Stytch.instance.handleDeepLink(data)) {
            showLoading()
            return
        }
    }
    super.onNewIntent(intent)
}
```

First set listener to Stytch
```
Stytch.instance.listener = this  // Stytch.StytchListener
```

Call login to request login
```
Stytch.instance.login(email)
```

#### Handle Callbacks

Stytch.StytchListener
- onSuccess(result: StytchResult) // Called after successful user authorization. Flow is finished.
- onFailure(error: StytchError) // Called when error occurred, you need to show error to user
- onMagicLinkSent(email: String) // Called after magic link sent to email

# Package com.stytch.sdk.consumer
Stytch's consumer SDK makes it simple to seamlessly onboard, authenticate, and engage users. This SDK provides the easiest way for you to use Stytch on Android. With just a few lines of code, you can easily authenticate your users and get back to focusing on the core of your product.

## Supported Authentication Products
- [Magic links](magicLinks)
    - Send/authenticate magic links via Email
- [OTPs](otp)
    - Send/authenticate one-time passcodes via SMS, WhatsApp, Email
- [Passwords](passwords)
    - Create or authenticate a user
    - Check password strength
    - Reset a password
- [Sessions](sessions)
    - Authenticate/refresh an existing session
    - Revoke a session (Sign out)
- [Biometrics](biometrics)
    - Register/authenticate with biometrics
- [OAuth](oauth)
    - Register/authenticate with native Google OneTap
    - Register/authenticate with our supported third-party OAuth providers (Amazon, BitBucket, Coinbase, Discord, Facebook, Github, GitLab, Google, LinkedIn, Microsoft, Salesforce, Slack, or Twitch)
- [User Management](userManagement)
    - Get or fetch the current user object (sync/cached or async options available)
    - Delete factors by id from the current user

## Using the Consumer SDK
The `StytchClient` object is your entrypoint to the Stytch Consumer SDK and is how you interact with all of our supported authentication products.

Each endpoint is explained in their respective READMEs and inline-documentation, but there are a few special methods on the `StytchClient` object to document here.

### **Configuration**
As mentioned in the [toplevel README](/README.md), before making any Stytch authentication requests, you must configure the `StytchClient`:
```kotlin
StytchClient.configure(context: Context, publicToken: String)
```
This configures the API for authenticating requests and the encrypted storage helper for persisting session data across app launches.

### **Handling Deeplinks**
`StytchClient.handle()` is the method you call for parsing out and authenticating deeplinks that your application receives. The currently supported deeplink types are: Email Magic Links, Third-Party OAuth, and Password resets. This method returns a [DeeplinkHandledStatus](../common/DeeplinkHandledStatus.kt) class which details the result of the authentication call.

For Email Magic Links and Third-Party OAuth deeplinks, it will return a `Handled` class containing either the authenticated response or error.

For Password Reset deeplinks, it will return a `ManualHandlingRequired` class containing the relevant token, so that you can provide an appropriate UI to the user for resetting their password. The returned token is used for making the subsequent `StytchClient.passwords.resetByEmail()` call.

Any other link types passed to this method will return a `NotHandled` class.

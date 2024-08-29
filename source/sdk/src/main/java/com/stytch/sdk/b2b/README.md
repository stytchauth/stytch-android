# Package com.stytch.sdk.b2b
Stytch's B2B SDK makes it simple to seamlessly onboard, authenticate, and engage users. This SDK provides the easiest way for you to use Stytch on Android. With just a few lines of code, you can easily authenticate your users and get back to focusing on the core of your product.

## Supported Authentication Products
- [Magic links](magicLinks)
    - Send/authenticate magic links via Email
- [Sessions](sessions)
    - Authenticate/refresh an existing session
    - Revoke a session (Sign out)
- [Member](member)
    - Get or fetch the current user object (sync/cached or async options available)
- [Organization](organization)
    - Get or fetch the current user's organization

## Using the B2B SDK
The `StytchB2BClient` object is your entrypoint to the Stytch B2B SDK and is how you interact with all of our supported authentication products.

Each endpoint is explained in their respective READMEs and inline-documentation, but there are a few special methods on the `StytchB2BClient` object to document here.

### **Configuration**
As mentioned in the [toplevel README](/README.md), before making any Stytch authentication requests, you must configure the `StytchB2BClient`:
```kotlin
StytchB2BClient.configure(context, publicToken)
```
This configures the API for authenticating requests and the encrypted storage helper for persisting session data across app launches.

### **Handling Deeplinks**
`StytchB2BClient.handle()` is the method you call for parsing out and authenticating deeplinks that your application receives. The currently supported deeplink types are: B2B Email Magic Links. This method returns a [DeeplinkHandledStatus](../common/DeeplinkHandledStatus.kt) class which details the result of the authentication call.

For B2B Email Magic Links, it will return a `Handled` class containing either the authenticated response or error.

Any other link types passed to this method will return a `NotHandled` class.

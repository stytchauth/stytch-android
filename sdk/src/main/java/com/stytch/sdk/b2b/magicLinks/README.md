# Package com.stytch.sdk.b2b.magicLinks
The [B2BMagicLinks](B2BMagicLinks.kt) interface provides methods for sending and authenticating users with Email Magic Links.

Call the `StytchB2BClient.magicLinks.email.loginOrSignup()` method to request an email magic link for a user to log in or create an account, based on if the email is associated with a user already. A new or pending user will receive a signup magic link. An active user will receive a login magic link.

If you have connected your deeplink handler with `StytchB2BClient`, the resulting magic link should be detected by your application and automatically authenticated (via the `StytchB2BClient.handle()` method). See the instructions in the [top-level README](/README.md) for information on handling deeplink intents.

If you are not using our deeplink handler, you must parse out the token from the deeplink yourself and pass it to the `StytchB2BClient.magicLinks.authenticate()` method.
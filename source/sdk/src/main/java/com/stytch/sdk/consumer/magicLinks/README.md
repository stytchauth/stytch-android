# Package com.stytch.sdk.consumer.magicLinks
The [MagicLinks](MagicLinks.kt) interface provides methods for sending and authenticating users with Email Magic Links.

Call the `StytchClient.magicLinks.email.loginOrCreate()` method to request an email magic link for a user to log in or create an account, based on if the email is associated with a user already. A new or pending user will receive a signup magic link. An active user will receive a login magic link.

Call the `StytchClient.magicLinks.email.send()` method to request an email magic link for an existing user to authenticate.

If you have connected your deeplink handler with `StytchClient`, the resulting magic link should be detected by your application and automatically authenticated (via the `StytchClient.handle()` method). See the instructions in the [top-level README](/README.md) for information on handling deeplink intents.

If you are not using our deeplink handler, you must parse out the token from the deeplink yourself and pass it to the `StytchClient.magicLinks.authenticate()` method.
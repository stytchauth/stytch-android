# Package com.stytch.sdk.consumer.passwords
The [Passwords](Passwords.kt) interface provides methods for authenticating, creating, resetting, and performing strength checks of passwords.

Stytch supports creating, storing, and authenticating passwords, as well as support for account recovery (password reset) and account deduplication with passwordless login methods.

Our implementation of passwords has built-in breach detection powered by [HaveIBeenPwned](https://haveibeenpwned.com) on both sign-up and login, to prevent the use of compromised credentials and uses configurable strength requirements (either Dropbox’s [zxcvbn](https://github.com/dropbox/zxcvbn) or adjustable LUDS) to guide members towards creating passwords that are easy for humans to remember but difficult for computers to crack.

Call the `StytchClient.passwords.create()` method to create a new user with an email address and password. Only use this for creating new users. Existing passwordless users who wish to create a password need to go through the reset password flow.

Call the `StytchClient.passwords.authenticate()` method to authenticate a user with their email address and password. This method will return an error if the user’s credentials appeared in the HaveIBeenPwned dataset or if a duplicate user is found (eg: Email Magic Links, OAuth).

Call the `StytchClient.passwords.resetByEmailStart()` method to initiate a password reset for the email address provided. This will trigger an email to be sent to the address, containing a magic link that will allow them to set a new password and authenticate.

Call the `StytchClient.passwords.resetBySession()` method to reset the user’s password using their existing session. The endpoint will error if the session does not have a password, email magic link, or email OTP authentication factor that has been issued within the last 5 minutes.

If you have connected your deeplink handler with `StytchClient`, the resulting magic link should be detected by your application and automatically parsed (via the `StytchClient.handle()` method). See the instructions in the [top-level README](/README.md) for information on handling deeplink intents. You should get a `DeeplinkHandledStatus.ManualHandlingRequired` result, which includes the necessary token for resetting the user's password. If you are not using our deeplink handler, you must parse out the token from the deeplink yourself.

Once you have a password reset token, and have collected the user's new desired password, you can call the `StytchClient.passwords.resetByEmail()` method to finish resetting their password.

Call the `StytchClient.passwords.resetByExistingPassword()` method to reset a user's password using their existing email address and password combination.

Call the `StytchClient.passwords.strengthCheck()` method to check whether or not the user’s provided password is valid, and to provide feedback to the user on how to increase the strength of their password.